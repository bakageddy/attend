let result_teacher_handle = document.getElementById("result_display");
let teacher_id_handle = document.getElementById("search__teacher__id");
let teacher_name_handle = document.getElementById("search__subject__name");

teacher_id_handle.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams(
		{ teacherid: search_string }
	);

	let result = await fetch(
		"/app/api/teacher/search?" + url_params.toString(),
		{ method: "GET" }
	);
	let json_results = await result.json();

	console.log(json_results);
	result_teacher_handle.innerText = json_results.toString();
	return;
});

teacher_name_handle.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: search_string }
	);

	let results = await fetch(
		"/app/api/subject/search?" + url_params.toString(),
		{ method: "GET" }
	);
	let json_results = results.json();

	console.log(json_results);
	result_teacher_handle.innerText = json_results.toString();

	return;
})
