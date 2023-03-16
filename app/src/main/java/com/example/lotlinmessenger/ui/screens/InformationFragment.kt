package com.example.lotlinmessenger.ui.screens

import com.example.lotlinmessenger.R
import com.example.lotlinmessenger.ui.screens.base.BaseFragment
import com.example.lotlinmessenger.utillits.APP_ACTIVITY


class InformationFragment : BaseFragment(R.layout.fragment_information) {
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Информация"
    }
}