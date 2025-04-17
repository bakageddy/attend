let attendance_rollno = document.getElementById("attendance__input__rollno");
let attendance_teacherid = document.getElementById("attendance__input__teacherid");
let attendance_subjectid = document.getElementById("attendance__input__subjectid");

attendance_rollno.addEventListener('rollno_selected', event => {
	console.log(event["detail"]["rollno"]);
	document.getElementById("attendance__input__rollno").value = 10;
});

attendance_teacherid.addEventListener('teacherid_selected', event => {
	console.log(event["detail"]["teacherid"]);
	document.getElementById("attendance__input__teacherid").value = 10;
});

attendance_subjectid.addEventListener('subjectid_selected', event => {
	console.log(event["detail"]["subjectid"]);
	document.getElementById("attendance__input__subjectid").value = 10;
});
