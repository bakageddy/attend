const handleBatchStudentDelete = async event => {
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

	let result = await fetch("/app/api/batch/student?" + params.toString(), {method:"DELETE"});
	if (result.status != 200) {
		alert("Something went wrong!");
	} else {
		alert("Batch Students deleted");
	}
}

document.getElementById("batch__data__crud__delete__button").onclick = handleBatchStudentDelete;
