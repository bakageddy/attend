import Component from "@glimmer/component";
import { action } from "@ember/object"

export default class TeacherComponent extends Component {
  @action
  handleClick(ev) {
    let teacherid = ev.currentTarget.querySelector(".teacher__result__id").innerText;
    teacherid = Number.parseInt(teacherid);

    let attendance_teacherid_input = document.getElementById("attendance__form__teacherid");
    let batch_attendance_teacherid_input = document.getElementById("batch__attendance__form__teacherid");

    attendance_teacherid_input.value = teacherid;
    batch_attendance_teacherid_input.value = teacherid;
    return;
  }
}
