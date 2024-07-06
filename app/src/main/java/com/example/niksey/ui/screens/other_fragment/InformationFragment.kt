package com.example.niksey.ui.screens.other_fragment

import com.example.niksey.R
import com.example.niksey.ui.screens.base_fragment.BaseFragment
import com.example.niksey.utillits.APP_ACTIVITY


class InformationFragment : BaseFragment(R.layout.fragment_information) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.information)
    }
}