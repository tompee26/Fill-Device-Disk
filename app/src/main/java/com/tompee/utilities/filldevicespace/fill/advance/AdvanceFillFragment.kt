package com.tompee.utilities.filldevicespace.fill.advance

import android.os.Bundle
import android.view.View
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import com.tompee.utilities.filldevicespace.common.ui.BaseFragment
import com.tompee.utilities.filldevicespace.databinding.FragmentAdvanceBinding
import javax.inject.Inject

/**
 * Advance Fill fragment
 */
internal class AdvanceFillFragment @Inject constructor(
    private val viewModelFactory: ViewModelFactory
) : BaseFragment<FragmentAdvanceBinding>() {

    override val layoutId: Int = R.layout.fragment_advance

    private val viewModel by lazy { viewModelFactory.get<AdvanceFillViewModel>(Scope.THIS) }

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