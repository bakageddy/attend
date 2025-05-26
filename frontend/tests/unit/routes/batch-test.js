import { module, test } from 'qunit';
import { setupTest } from 'frontend/tests/helpers';

module('Unit | Route | batch', function (hooks) {
  setupTest(hooks);

  test('it exists', function (assert) {
    let route = this.owner.lookup('route:batch');
    assert.ok(route);
  });
});
