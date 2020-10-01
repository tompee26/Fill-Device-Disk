package com.tompee.utilities.filldevicespace.fill.easy

import android.os.Bundle
import android.view.View
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import com.tompee.utilities.filldevicespace.common.ui.BaseFragment
import com.tompee.utilities.filldevicespace.databinding.FragmentEasyBinding
import javax.inject.Inject

/**
 * Easy Fill fragment
 */
internal class EasyFillFragment @Inject constructor(
    private val viewModelFactory: ViewModelFactory
) : BaseFragment<FragmentEasyBinding>() {

    override val layoutId: Int = R.layout.fragment_easy

    private val viewModel by lazy { viewModelFactory.get<EasyFillViewModel>(Scope.THIS) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewModel = viewModel
        with(viewModel) {
            percentage.observeBy { viewBinding.circleView.setValue(it) }
            isFilling.observeBy {
                viewBinding.start.setImageResource(if (it) R.drawable.ic_stop else R.drawable.ic_play)
                if (it) viewBinding.start.setOnClickListener { viewModel.stopFill() }
                else viewBinding.start.setOnClickListener { viewModel.startFill() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.invalidate()
    }
}