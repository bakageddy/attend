import Component from "@glimmer/component";
import { action } from "@ember/object"

export default class StudentComponent extends Component {
  @action
  handleClick(ev) {
    let rollno = ev.currentTarget.querySelector(".student__result__id").innerText;
    let attendance_rollno_input = document.getElementById("attendance__form__rollno");
    if (attendance_rollno_input !== null) {
      attendance_rollno_input.value = Number.parseInt(rollno);
    }
    return;
  }

  @action
  handleDragStart(ev) {
    let rollno = ev.target.querySelector(".student__result__id");
    ev.dataTransfer.setData("Text", rollno.innerText);
  }
}
