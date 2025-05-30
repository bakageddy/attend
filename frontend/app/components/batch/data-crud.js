import Component from "@glimmer/component";
import { service } from "@ember/service";
import { action } from "@ember/object";

export default class DataCrudComponent extends Component {
  @service('batch_students') students;
  @service("error_service") error;
  @service router;

  @action
  async batch_crud_add(event) {
    let ids = [];
    console.log(this.students.getElements());
    for (const elem of this.students.getElements()) {
      ids.push(elem.id);
    }

    let params = new URLSearchParams({
      "batchid": document.getElementById("batch__crud__batchid__input").value,
      "teacherid": document.getElementById("batch__crud__teacherid__input").value,
      "rollno[]": ids.join(","),
    }).toString();

    let resp = await fetch(
      "http://localhost:8080/app/api/batch/student?" + params,
      {method: "POST"}
    );

    if (resp.statusCode != 200) {
      if (resp.statusCode !== 400) {
        this.error.set("Something Went Wrong! Double check the form for more errors");
        router.transitionTo("error");
      } else if (resp.statusCode !== 500) {
        this.error.set("Something Went Wrong with the server! We are fixing it!");
        router.transitionTo("error");
      }
    }
  }

  @action
  async batch_crud_delete(event) {
    let ids = [];
    for (const elem of this.students.getElements()) {
      ids.push(elem.id);
    }

    let params = new URLSearchParams({
      "batchid": document.getElementById("batch__crud__batchid__input").value,
      "teacherid": document.getElementById("batch__crud__teacherid__input").value,
      "rollno[]": ids.join(","),
    }).toString();

    let resp = await fetch(
      "http://localhost:8080/app/api/batch/student?" + params,
      {method: "DELETE"}
    );

    if (resp.statusCode != 200) {
      if (resp.statusCode !== 400) {
        this.error.set("Something Went Wrong! Double check the form for more errors");
        this.router.transitionTo("error");
      } else if (resp.statusCode !== 500) {
        this.error.set("Something Went Wrong with the server! We are fixing it!");
        this.router.transitionTo("error");
      }
    }
  }

  @action
  handle_batch_input(ev) {
    this.batchid = ev.target.value;
  }

  @action
  handle_teacher_input(ev) {
    this.teacherid = ev.target.value;
  }
}
