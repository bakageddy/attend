import Component from "@glimmer/component";
import { action } from "@ember/object";
import { service } from "@ember/service";

export default class StudentComponent extends Component {
  @service('drag_drop') global_state;

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
    let id = ev.target.querySelector(".student__result__id").innerText;
    id = Number.parseInt(id);

    let name = ev.target.querySelector(".student__result__name").innerText;
    this.global_state.setElem({id: id, name: name});
  }
}
