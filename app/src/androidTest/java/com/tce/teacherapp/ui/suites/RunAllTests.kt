package com.tce.teacherapp.ui.suites

import com.tce.teacherapp.ui.ListFragmentTests
import kotlinx.coroutines.InternalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@UseExperimental(InternalCoroutinesApi::class)
@RunWith(Suite::class)
@Suite.SuiteClasses(
    ListFragmentTests::class
)
class RunAllTests