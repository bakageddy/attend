import Component from "@glimmer/component";
import { action } from "@ember/object";
import { tracked } from "@glimmer/tracking";

export default class AttendanceComponent extends Component {

  @tracked studentid  = undefined;
  @tracked teacherid  = undefined;
  @tracked subjectid  = undefined;
  @tracked period     = undefined;
  @tracked batchid    = undefined;
  date                = undefined;

  @action
  async delete_attendance(_) {
    if (this.studentid === undefined
      || this.teacherid === undefined
      || this.subjectid === undefined
    ) {
      // TODO: Render Err Dialog
      return;
    }

    this.period = document.getElementById("attendance__form__period").value;
    this.date = document.getElementById("attendance__form__date").value;
    let params = new URLSearchParams({
      "rollno":     this.studentid,
      "teacherid":  this.teacherid,
      "subjectid":  this.subjectid,
      "period":     this.period,
      "date":       this.date
    });

    let resp = await fetch("http://localhost:8080/app/api/attendance/?" + params.toString(), {
      method: "DELETE"
    });

    if (resp.status != 200) {
      // Render dialog
    }
    return;
  }

  @action
  async insert_attendance(_) {
    if (this.studentid === undefined
      || this.teacherid === undefined
      || this.subjectid === undefined
    ) {
      // TODO: Render Err Dialog
      return;
    }

    this.period = document.getElementById("attendance__form__period").value;
    this.date = document.getElementById("attendance__form__date").value;
    let params = new URLSearchParams({
      "rollno":     this.studentid,
      "teacherid":  this.teacherid,
      "subjectid":  this.subjectid,
      "period":     this.period,
      "date":       this.date
    });

    let resp = await fetch("http://localhost:8080/app/api/attendance/?" + params.toString(), {
      method: "POST"
    });

    if (resp.statusCode != 200) {
      // Render dialog
    }
    return;
  }

  @action
  async insert_batch_attendance(_) {
    if (this.batchid === undefined
      || this.teacherid === undefined
      || this.subjectid === undefined
    ) {
      // TODO: Render Err Dialog
      return;
    }

    this.period = document.getElementById("batch__attendance__form__period").value;
    this.date = document.getElementById("batch__attendance__form__date").value;

    let params = new URLSearchParams({
      "batchid":    this.batchid,
      "teacherid":  this.teacherid,
      "subjectid":  this.subjectid,
      "period":     this.period,
      "date":       this.date
    });

    let resp = await fetch("http://localhost:8080/app/api/attendance/batch/?" + params.toString(), {
      method: "POST"
    });

    if (resp.statusCode != 200) {
      // Render dialog
    }
    return;
  }
}
