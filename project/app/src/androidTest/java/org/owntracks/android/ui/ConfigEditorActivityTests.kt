package org.owntracks.android.ui

import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn
import com.adevinta.android.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import com.adevinta.android.barista.interaction.BaristaEditTextInteractions.writeTo
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.preferences.Preferences
import org.owntracks.android.testutils.TestWithAnActivity
import org.owntracks.android.ui.preferences.editor.EditorActivity

@MediumTest
@RunWith(AndroidJUnit4::class)
class ConfigEditorActivityTests :
    TestWithAnActivity<EditorActivity>(EditorActivity::class.java, startActivity = false) {
  @Test
  fun configurationManagementCanEditASetType() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::experimentalFeatures.name)
    writeTo(R.id.inputValue, "this, that,    other")
    clickDialogPositiveButton()
    assertContains(
        R.id.effectiveConfiguration, "\"experimentalFeatures\" : [ \"other\", \"that\", \"this\" ]")
  }

  @Test
  fun configurationManagementCanEditTheMode() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::mode.name)
    writeTo(R.id.inputValue, "0")
    clickDialogPositiveButton()
    assertContains(R.id.effectiveConfiguration, "\"mode\" : 0")
  }

  @Test
  fun configurationManagementCanEditTheModeToADefault() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::mode.name)
    writeTo(R.id.inputValue, "not a number")
    clickDialogPositiveButton()
    assertContains(R.id.effectiveConfiguration, "\"mode\" : 0")
  }

  @Test
  fun configurationManagementCanEditTheMQTTProtocolLevel() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::mqttProtocolLevel.name)
    writeTo(R.id.inputValue, "4")
    clickDialogPositiveButton()
    assertContains(R.id.effectiveConfiguration, "\"mqttProtocolLevel\" : 4")
  }

  @Test
  fun configurationManagementCanEditTheMQTTProtocolLevelToADefault() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::mqttProtocolLevel.name)
    writeTo(R.id.inputValue, "not a number")
    clickDialogPositiveButton()
    assertContains(R.id.effectiveConfiguration, "\"mqttProtocolLevel\" : 3")
  }

  @Test
  fun configurationManagementCanEditAStringType() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::host.name)
    writeTo(R.id.inputValue, "example.com")
    clickDialogPositiveButton()

    assertContains(R.id.effectiveConfiguration, "\"host\" : \"example.com\"")
  }

  @Test
  fun configurationManagementCanEditABooleanType() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::cmd.name)
    writeTo(R.id.inputValue, "false")
    clickDialogPositiveButton()
    assertContains(R.id.effectiveConfiguration, "\"cmd\" : false")
  }

  @Test
  fun configurationManagementCanEditAFloatType() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::osmTileScaleFactor.name)
    writeTo(R.id.inputValue, "0.5")
    clickDialogPositiveButton()

    assertContains(R.id.effectiveConfiguration, "\"osmTileScaleFactor\" : 0.5")
  }

  @Test
  fun configurationManagementShowsAnErrorWhenPuttingANonFloatIntoAFloat() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, Preferences::osmTileScaleFactor.name)
    writeTo(R.id.inputValue, "not a float")
    clickDialogPositiveButton()
    assertContains(
        com.google.android.material.R.id.snackbar_text, R.string.preferencesEditorValueError)
  }

  @Test
  fun configurationManagementShowsAnErrorWhenSettingAnInvalidKey() {
    launchActivity()
    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.preferencesEditor)
    writeTo(R.id.inputKey, "Not a valid key")
    writeTo(R.id.inputValue, "not a float")
    clickDialogPositiveButton()
    assertContains(
        com.google.android.material.R.id.snackbar_text, R.string.preferencesEditorKeyError)
  }

  @Test
  fun editorActivityShowsDefaultConfig() {
    launchActivity()
    assertContains(R.id.effectiveConfiguration, "\"_type\" : \"configuration\"")
  }

  @Test
  fun editorCanExportConfig() {
    launchActivity()
    val chooserIntentMatcher =
        allOf(hasAction(Intent.ACTION_CREATE_DOCUMENT), hasExtra(Intent.EXTRA_TITLE, "config.otrc"))
    intending(anyIntent()).respondWithFunction { Instrumentation.ActivityResult(0, null) }

    openActionBarOverflowOrOptionsMenu(baristaRule.activityTestRule.activity)
    clickOn(R.string.exportConfiguration)

    intended(chooserIntentMatcher)
  }
}
