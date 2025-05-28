import Component from "@glimmer/component";
import {action} from "@ember/object"

export default class DropComponent extends Component {
  @action
  handleDrop(ev) {
    if (ev.target.id === "batch__drop__zone") {
      if (ev.dataTransfer !== null) {
        console.log(ev.dataTransfer.getData("text"));
      }
    }
  }
}
