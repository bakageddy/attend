"use strict";

const attendanceFormCreate = async () => {
	let rollno = document.getElementById("attendance__input__rollno").value;
	if (!rollno) {
		alert("Set Rollno!");
		return;
	}

	let teacherid = document.getElementById("attendance__input__teacherid").value;
	if (!teacherid) {
		alert("Set Teacher ID!");
		return;
	}

	let subjectid = document.getElementById("attendance__input__subjectid").value;
	if (!subjectid) {
		alert("Set Subject ID");
		return;
	}

	let date = document.getElementById("attendance__input__date").value;
	if (!date) {
		alert("Set Date!");
		return;
	}

	let period = document.getElementById("attendance__input__period").value;
	if (!period) {
		alert("Set Period");
		return;
	}

	let params = new URLSearchParams({
		"rollno": rollno,
		"teacherid": teacherid,
		"subjectid": subjectid,
		"period": period,
		"date": date
	});

	let result = await fetch("/app/api/attendance/?" + params.toString(), {
		method: "POST",
	});

	if (result.status == 400) {
		// TODO
	} else if (result.status == 200) {
		alert("Inserted!");
	}

}

const attendanceFormDelete = async () => {
	let rollno = document.getElementById("attendance__input__rollno").value;
	if (!rollno) {
		alert("Set Rollno!");
		return;
	}

	let teacherid = document.getElementById("attendance__input__teacherid").value;
	if (!teacherid) {
		alert("Set Teacher ID!");
		return;
	}

	let subjectid = document.getElementById("attendance__input__subjectid").value;
	if (!subjectid) {
		alert("Set Subject ID");
		return;
	}

	let date = document.getElementById("attendance__input__date").value;
	if (!date) {
		alert("Set Date!");
		return;
	}

	let period = document.getElementById("attendance__input__period").value;
	if (!period) {
		alert("Set Period");
		return;
	}

	let params = new URLSearchParams({
		"rollno": rollno,
		"teacherid": teacherid,
		"subjectid": subjectid,
		"period": period,
		"date": date
	});

	let result = await fetch("/app/api/attendance/?" + params.toString(), {
		method: "DELETE",
	});

	if (result.status == 400) {
		// TODO
	}
	if (result.status == 200) {
		alert("Deleted!");
	}
}
