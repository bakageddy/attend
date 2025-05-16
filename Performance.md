# Performance Techniques
## Applied
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
