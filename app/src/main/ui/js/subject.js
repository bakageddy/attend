let result = document.getElementById("result_display");
let subject_id_search = document.getElementById("search__subject__id");
let subject_code_search = document.getElementById("search__subject__code");
let subject_name_search = document.getElementById("search__subject__name");

student_name_search.addEventListener('input', async (event) => {
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
	result.innerText = json_results.toString();

	return;
})
