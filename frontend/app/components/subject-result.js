import Component from "@glimmer/component";
import { action } from "@ember/object";

export default class SubjectResultComponent extends Component {
  @action
  handleClick(ev) {
    let subjectid = ev.currentTarget.querySelector(".subject__result__id").innerText;
    subjectid = Number.parseInt(subjectid);

    let attendance_subjectid_input = document.getElementById("attendance__form__subjectid");
    let batch_attendance_subjectid_input = document.getElementById("batch__attendance__form__subjectid");

    attendance_subjectid_input.value = subjectid;
    batch_attendance_subjectid_input.value = subjectid;
  }
}
