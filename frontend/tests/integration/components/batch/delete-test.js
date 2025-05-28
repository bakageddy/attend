import { module, test } from 'qunit';
import { setupRenderingTest } from 'frontend/tests/helpers';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

module('Integration | Component | batch/delete', function (hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function (assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`<Batch::Delete />`);

    assert.dom().hasText('');

    // Template block usage:
    await render(hbs`
      <Batch::Delete>
        template block text
      </Batch::Delete>
    `);

    assert.dom().hasText('template block text');
  });
});
