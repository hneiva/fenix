/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.tabstray.browser

import io.mockk.mockk
import io.mockk.verify
import mozilla.components.browser.state.state.createTab
import mozilla.components.browser.tabstray.TabsTray
import mozilla.components.support.test.ext.joinBlocking
import mozilla.components.support.test.rule.MainCoroutineRule
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mozilla.fenix.tabstray.TabsTrayAction
import org.mozilla.fenix.tabstray.TabsTrayStore

class InactiveTabsBindingTest {
    val store = TabsTrayStore()
    val tray: TabsTray = mockk(relaxed = true)
    val binding = InactiveTabsBinding(store, tray)

    @get:Rule
    val coroutinesTestRule = MainCoroutineRule()

    @After
    fun teardown() {
        binding.stop()
    }

    @Test
    fun `WHEN the store is updated THEN notify the tabs tray`() {
        assertTrue(store.state.inactiveTabs.isEmpty())

        store.dispatch(TabsTrayAction.UpdateInactiveTabs(listOf(createTab("https://mozilla.org")))).joinBlocking()

        binding.start()

        assertTrue(store.state.inactiveTabs.isNotEmpty())

        verify { tray.updateTabs(any(), any()) }
    }

    @Test
    fun `WHEN non-inactive tabs are updated THEN do not notify the tabs tray`() {
        assertTrue(store.state.inactiveTabs.isEmpty())

        store.dispatch(TabsTrayAction.UpdatePrivateTabs(listOf(createTab("https://mozilla.org")))).joinBlocking()

        binding.start()

        assertTrue(store.state.inactiveTabs.isEmpty())

        verify { tray.updateTabs(emptyList(), null) }
    }
}
