# Performance Techniques
## Applied
### Database
During searching with certain inputs, the server chokes up and returns a result in the magnitude of 20s
    -> Enable auto_explain shared library (dumps the execution plan for certain queries that can be specified)
    -> dump queries that takes over 1s (searching should be within 20ms imho)
    -> See parameters to the prepared statements that take over 20s because they are not present
    -> Database defaults to sequential scan
    -> The sequential scans only take place when the parameters is not in the table 
    -> Improve statistics target from 1000 to 100_000 (The Student table is 100_000_000 elements big)
    -> Database defaults to sequential scan still
    -> This is due to the use of prepared statements with it's generic plan, which is not based on the stats of the database
    -> `SET plan_cache_mode = force_custom_plan;`
    -> This forces the database to come up with a custom plan for the given input each time.
    -> Increases planning time for all queries, but decreases sudden spikes that go to 6-20s
```c++
// explain analyze execute membership('Vijay%');
                                                       QUERY PLAN
------------------------------------------------------------------------------------------------------------------------
 Limit  (cost=0.00..4.20 rows=1 width=4) (actual time=6393.018..6393.019 rows=0 loops=1)
   ->  Seq Scan on student  (cost=0.00..2098987.00 rows=500000 width=4) (actual time=6393.016..6393.017 rows=0 loops=1)
         Filter: (name ~~ $1)
         Rows Removed by Filter: 100000000
 Planning Time: 0.087 ms
 Execution Time: 6393.087 ms
(6 rows)
```


### Indices
For searching,
    -> by pattern, included index of type btree-text-pattern-ops, 4 secs to 1ms..
    -> by pattern, checked whether if a result is possible with LIMIT 1, if yes, commit to search. (NOT NECESSARY WITH NON-COVERING INDEX), theres jitter with certain inputs, where the db resorts to sequential scan
    -> by pattern, use INCLUDE part of the index creation to include the data in the index without it being in the search key of the index
### LRU / MRU cache
Cache Eviction
    -> LRU
    -> also includes MRU for Batch Searching since once a batch is used, it's not required anymore
Cache Invalidation
    -> nodes have a max ttl of 5mins (attendance in my college takes place in the last 5 mins, so searching for students takes place under load in this time period)
To make the cache O(1)
    -> put tries to evict exactly one stale node every time it get's invoked, instead of trying to evict all stale nodes in the cache at once.
    -> get checks if the node is stale before returning

# Queries used
## Attendance
```sql
INSERT INTO Attendance(Day, RollNo, Period, SubjectID, TeacherID) 
VALUES(?::date, ?, ?::period, ?, ?);
```

```sql
DELETE FROM Attendance 
WHERE Day=?::date AND 
    RollNo=? AND 
    TeacherID=? AND 
    SubjectID=? AND 
    Period=?::period;
```

## Batch
### Search by BatchID
```sql
SELECT BatchID, TeacherID, Name 
FROM Batch 
WHERE BatchID = ?;
```

### Search by BatchName

Check for existing records.
```sql
SELECT 1
FROM Batch 
WHERE Name LIKE ? 
LIMIT 1;
```

Actual search
```sql
SELECT BatchID, TeacherID, Name 
FROM Batch 
WHERE Name LIKE ? 
LIMIT 20;
```

### Search by TeacherID
```sql
SELECT BatchID, Name FROM Batch 
WHERE TeacherID = ?
LIMIT 20;
```

### Batch CRUD
```sql
INSERT INTO Batch(Name, TeacherID)
VALUES(?, ?)
RETURNING BatchID;
```

```sql
DELETE FROM Batch 
WHERE BatchID = ? AND TeacherID = ?;
```

## Batchdata

### Fetch Owner
```sql
SELECT Batch.TeacherID, Student.RollNo, Student.Name 
FROM Batch
JOIN BatchData on Batch.BatchID = BatchData.BatchID 
JOIN Student ON BatchData.RollNo = Student.RollNo 
WHERE BatchData.BatchID = ?;
```

```sql
INSERT INTO BatchData(batchid, rollno) VALUES(?, ?);
```

```sql
DELETE 
FROM BatchData 
WHERE BatchID = ? AND RollNo = ?;
```

## Student (Search)

```sql
SELECT Name 
FROM Student 
WHERE RollNo = ?;
```

```sql
SELECT 1 FROM Student 
WHERE Name LIKE ? LIMIT 1;
```

```sql
SELECT RollNo, Name 
FROM Student 
WHERE Name LIKE ? 
ORDER BY RollNo 
LIMIT 20;
```

## Subject (Search)

```sql
SELECT SubjectID, SubjectCode, Name
FROM Subject 
WHERE SubjectID = ?;
```

```sql
SELECT 1
FROM Subject 
WHERE Name LIKE ? 
LIMIT 1;
```

```sql
SELECT SubjectID, SubjectCode, Name 
FROM Subject 
WHERE Name LIKE ? 
LIMIT 20;
```

```sql
SELECT 1
FROM Subject
WHERE SubjectCode LIKE ?
LIMIT 1;
```

```sql
SELECT SubjectID, SubjectCode, Name
FROM Subject
WHERE SubjectCode LIKE ?
LIMIT 20;
```

## Teacher (Search)

```sql
SELECT Name 
FROM Teacher 
WHERE TeacherID = ?;
```

```sql
SELECT 1
FROM Teacher
WHERE Name LIKE ?
LIMIT 1;
```

```sql
SELECT TeacherID, Name
FROM Teacher
WHERE Name LIKE ?
LIMIT 20;
```
