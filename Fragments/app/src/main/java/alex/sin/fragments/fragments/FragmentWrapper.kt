package alex.sin.fragments.fragments

import alex.sin.fragments.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentWrapper : Fragment() {

    private var backstackCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.tab_navigation, container, false)

        backstackCount = if (call > 0) arguments?.getInt("backstackCount") ?: 0 else 0

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.backstack).text = (1..backstackCount).toList().joinToString(" -> ") { it.toString() }
        view.findViewById<Button>(R.id.next).setOnClickListener {
            val bsc = this.backstackCount
            parentFragmentManager.beginTransaction()
                .replace(R.id.tab_navigation, FragmentWrapper().apply {
                    arguments = Bundle().apply {
                        putInt("backstackCount", bsc + 1)
                    }
                })
                .addToBackStack("")
                .commit()
        }
        view.findViewById<Button>(R.id.prev).setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    @Suppress("ObjectPropertyName")
    companion object {
        private var _call = 0
        private val call
            get() = _call++
    }
}