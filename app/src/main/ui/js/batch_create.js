"use strict"

async function handle_batch_delete() {
	let batchname = document.getElementById("batch__crud__name").value;
	let teacherid = document.getElementById("batch__crud__teacherid").value;
	let batchid = document.getElementById("batch__crud__batchid").value;
	let params = new URLSearchParams({
		"teacherid": teacherid,
		"batchid": batchid
	});

	let resp = await fetch("/app/api/batch?" + params.toString(), { method: "DELETE" });
	if (resp.status == 400) {
		alert("You have sent invalid data");
	} else if (resp.status == 500) {
		alert("Something went wrong");
	} else if (resp.status == 200) {
		console.log("Deleted!");
	}
	return;
}

async function handle_batch_create() {
	let batchname = document.getElementById("batch__crud__name").value;
	let teacherid = document.getElementById("batch__crud__teacherid").value;
	let params = new URLSearchParams({
		"name": batchname,
		"teacherid": teacherid
	});

	let resp = await fetch(
		"/app/api/batch?" + params.toString(),
		{ method: "POST" }
	);
	let body = await resp.text();
	console.log(body);
}
