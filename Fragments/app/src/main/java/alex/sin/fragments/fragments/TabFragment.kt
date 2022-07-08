package alex.sin.fragments.fragments

import alex.sin.fragments.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class TabFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.clear_tab, container, false)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.tab_navigation, FragmentWrapper().apply {
                    val bundle = Bundle()
                    bundle.putInt("backstackCount", 1)
                    arguments = bundle
                })
                .commit()
        }

        return view
    }
}