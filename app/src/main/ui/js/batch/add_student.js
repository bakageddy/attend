const handleBatchStudentUpdate = async event => {
	let batchid = document.getElementById("batch__data__scratchpad__batchid").value;
	let dropzone = document.getElementById("student_dropzone");

	let elements = Array.from(
		dropzone.querySelectorAll(".student__element__rollno")
			.values()
			.map(e => e.textContent)
	);

	let params = new URLSearchParams({
		"batchid": batchid,
		"rollno[]": elements.join(",")
	});
	let result = await fetch("/app/api/batch/student?" + params.toString(), {method: "POST"});
	if (result.status != 201) {
		alert("Something wrong happened!");
	} else {
		alert("Batch Update Successful!");
	}
}

document.getElementById('batch__data__crud__create__button').onclick = handleBatchStudentUpdate;
