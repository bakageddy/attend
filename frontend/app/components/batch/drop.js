import Component from "@glimmer/component";
import {action} from "@ember/object"
import { service } from "@ember/service"

export default class DropComponent extends Component {
  @service('drag_drop') global_state;
  @service('batch_students') students;

  @action
  handleDrop(ev) {
    if (ev.target.id === "batch__drop__zone") {
      let val = this.global_state.getElem();
      console.log("Dropped: ", val);
      this.students.add(val);
    }
  }

  @action
  handleDragOver(ev) {
    ev.preventDefault();
  }
}
