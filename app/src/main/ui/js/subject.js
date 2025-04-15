let result_subject_handle = document.getElementById("result_display");
let subject_id_search = document.getElementById("search__subject__id");
let subject_code_search = document.getElementById("search__subject__code");
let subject_name_search = document.getElementById("search__subject__name");

subject_name_search.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams({
		pattern: `${search_string}%`
	});

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });
	let json_results = await result.json();

	console.log(json_results)
	result_subject_handle.innerText = json_results.toString();

	return;
});

subject_code_search.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams(
		{ code: search_string }
	);

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });
	let json_results = await result.json();

	console.log(json_results);
	result_subject_handle.innerText = json_results.toString();

	return;
});

subject_id_search.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ id: search_string }
	);
	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });
	let json_results = await result.json();

	console.log(json_results);
	result_subject_handle.innerText = json_results.toString();
	return;
});
