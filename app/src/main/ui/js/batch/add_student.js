const handleBatchStudentUpdate = event => {
	let batchid = document.getElementById("batch__data__scratchpad__batchid").value;
	let dropzone = document.getElementById("student_dropzone");
	let studentids = [];
	dropzone.querySelectorAll(".student__element").forEach(element => {
		let rollno = element.querySelector(".student__element__rollno").textContent;
		studentids.push(rollno);
	});

	let params = new URLSearchParams({
		"batchid": batchid,
		"rollno": rollno
	});
	fetch("/app/api/batch/student?" + ur)
}

document.getElementById('batch__data__crud__create__button').onclick = handleBatchStudentUpdate;
