let result_subject_handle = document.getElementById("result_display");
let subject_id_search = document.getElementById("search__subject__id");
let subject_code_search = document.getElementById("search__subject__code");
let subject_name_search = document.getElementById("search__subject__name");

const handleSubjectIdSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams({
		id: search_string
	});

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}

	let json_results = await result.json();

	result_subject_handle.innerHTML = '';
	result_subject_handle.innerHTML = render_subject(json_results);
	document.querySelectorAll(".subject__element").forEach(element => {
		element.addEventListener("click", event => {
			let subjectid = event.target.querySelector(".subject__element__id");
			if (!subjectid)
				return;
			let value = Number.parseInt(subjectid.textContent);
			let detail_event = new CustomEvent(
				'subjectid_selected',
				{ bubbles: true, detail: { subjectid: value } }
			);
			event.target.dispatchEvent(detail_event);
		})
	});
}

const handleSubjectCodeSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams(
		{ code: search_string }
	);

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}
	let json_results = await result.json();

	result_subject_handle.innerHTML = '';
	let html = '';
	Array.of(json_results).map(element => {
		html += render_subject(element);
	});
	result_subject_handle.innerHTML = html;

	document.querySelectorAll(".subject__element").forEach(element => {
		element.addEventListener("click", event => {
			let subjectid = event.target.querySelector(".subject__element__id");
			if (!subjectid)
				return;
			let value = Number.parseInt(subjectid.textContent);
			let detail_event = new CustomEvent(
				'subjectid_selected',
				{ bubbles: true, detail: { subjectid: value } }
			);
			event.target.dispatchEvent(detail_event);
		});
	});
	return;
}

const handleSubjectNameSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: search_string }
	);
	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}
	let json_results = await result.json();

	result_subject_handle.innerHTML = '';
	let html = '';
	Array.of(json_results).forEach(element => {
		html += render_subject(element);
	});
	result_subject_handle.innerHTML = html;

	document.querySelectorAll(".subject__element").forEach(element => {
		element.addEventListener("click", event => {
			let subjectid = event.target.querySelector(".subject__element__id");
			if (!subjectid)
				return;
			let value = Number.parseInt(subjectid.textContent);
			let detail_event = new CustomEvent(
				'subjectid_selected',
				{ bubbles: true, detail: { subjectid: value } }
			);
			event.target.dispatchEvent(detail_event);
		});
	});
}

subject_id_search.addEventListener('input', debounce((event) => handleSubjectIdSearch(event), 500));
subject_code_search.addEventListener('input', debounce((event) => handleSubjectCodeSearch(event), 500));
subject_name_search.addEventListener('input', debounce((event) => handleTeacherNameSearch(event), 500));

const render_subject = (json_data) => {
	return `
		<div class="subject__element result__element">
			<h3 class="subject__element__id">${json_data.subject_id}</h3>
			<h3 class="subject__element__code">${json_data.subject_code}</h3>
			<h3 class="subject__element__name">${json_data.name}</h3>
		</div>
	`;
}
