let result_teacher_handle = document.getElementById("result_display");
let teacher_id_handle = document.getElementById("search__teacher__id");
let teacher_name_handle = document.getElementById("search__teacher__name");


const handleTeacherIdSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams(
		{ id: search_string }
	);

	let result = await fetch(
		"/app/api/teacher/search?" + url_params.toString(),
		{ method: "GET" }
	);
	let json_result = await result.json();

	result_teacher_handle.innerHTML = render_teacher(json_result);
	document.querySelectorAll('.teacher__element').forEach(element => {
		element.addEventListener("click", event => {
			let teacherid = event.target.querySelector('div.teacher__element__id');
			if (!teacherid)
				return;

			let value = Number.parseInt(teacherid);
			let detail_event = new CustomEvent(
				'teacherid_selected',
				{ bubbles: true, detail: { teacherid: value } }
			);
			event.target.dispatchEvent(detail_event);
		});
	});
	return;
}

const handleTeacherNameSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: search_string }
	);

	let results = await fetch(
		"/app/api/teacher/search?" + url_params.toString(),
		{ method: "GET" }
	);

	if (results.status == 204) {
		// TODO: Implement Not found for names
	}

	let json_results = await results.json();

	result_teacher_handle.innerHTML = '';
	let html = "";
	json_results.map(element => {
		html += render_teacher(element);
	});
	result_teacher_handle.innerHTML = html;

	document.querySelectorAll('.teacher__element').forEach(element => {
		element.addEventListener("click", event => {
			let teacherid = event.target.querySelector('div.teacher__element__id');
			if (!teacherid)
				return;
			let value = Number.parseInt(teacherid.textContent);
			let detail_event = new CustomEvent(
				'teacherid_selected',
				{ bubbles: true, detail: { teacherid: value } }
			);
			event.target.dispatchEvent(detail_event);
		});
	});
	return;
}

const render_teacher = (json_data) => {
	return `
		<div class="teacher__element result__element">
			<h3 class="teacher__element__id">${json_data.teacher_id}</h3>
			<h4 class="teacher__element__name">${json_data.name}</h4>
		</div>
	`
}

teacher_id_handle.addEventListener('input', debounce(async (event) => await handleTeacherIdSearch(event)), 500);
teacher_name_handle.addEventListener('input', debounce(async (event) => await handleTeacherNameSearch(event)), 500); 
