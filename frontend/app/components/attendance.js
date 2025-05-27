import Component from "@glimmer/component";
import {action}  from "@ember/object";

export default class AttendanceComponent extends Component {
  @action
  async delete_attendance(_) {
    let teacherid = document.getElementById("attendance__form__teacherid").value;
    let subjectid = document.getElementById("attendance__form__subjectid").value;j
    let rollno = document.getElementById("attendance__form__rollno").value;
    let period = document.getElementById("attendance__form__period").value;
    let date = document.getElementById("attendance__form__date").value;
    let params = new URLSearchParams({
      "rollno": rollno,
      "teacherid": teacherid,
      "subjectid":subjectid,
      "period": period,
      "date": date
    });

    let _resp = await fetch("http://localhost:8080/app/api/attendance/?" + params.toString());
    return;
  }
}
