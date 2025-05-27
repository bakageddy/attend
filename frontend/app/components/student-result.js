import Component from "@glimmer/component";
import { action } from "@ember/object"

export default class StudentResultComponent extends Component {
  @action
  handleClick(ev) {
    let rollno = ev.currentTarget.querySelector(".student__result__id").innerText;
    let attendance_rollno_input = document.getElementById("attendance__form__rollno");
    attendance_rollno_input.value = Number.parseInt(rollno);
    return;
  }
}
