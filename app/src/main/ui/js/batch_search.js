"use strict";

let batchid_input = document.getElementById("batch__search__batchid");
let name_input = document.getElementById("batch__search__name");
let teacherid_input = document.getElementById("batch__search__teacherid");

function debounce_batch(func, timeout = 300) {
	let timer;
	return (...args) => {
		if (!timer) {
			func.apply(this, args);
		}
		clearTimeout(timer);
		timer = setTimeout(() => { timer = undefined }, timeout);
	};
}

const search_by_id = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		// TODO: handle invalid input
		return;
	}

	let url_params = new URLSearchParams({
		"batchid": search_string
	}).toString();

	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something bad happened!");
		return;
	}

	let results_json = await results.json();
	console.log(results_json);
	// TODO: render results
}

const search_by_name = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		"pattern": search_string
	});

	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something erronous happened!");
		return;
	}

	let results_json = await results.json();
	console.log(results_json);
	// TODO: render results
}

const search_by_teacherid = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		"teacherid": search_string
	});
	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something erronous happened!");
		return;
	}

	let results_json = await results.json();
	console.log(results_json);
	// TODO: render results
}

batchid_input.addEventListener("input", debounce_batch(async (event) => await search_by_id(event), 500));
name_input.addEventListener("input", debounce_batch(async (event) => await search_by_name(event), 500));
teacherid_input.addEventListener("input", debounce_batch(async (event) => await search_by_teacherid(event), 500));
