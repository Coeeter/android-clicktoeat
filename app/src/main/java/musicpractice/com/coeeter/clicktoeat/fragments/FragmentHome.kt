package musicpractice.com.coeeter.clicktoeat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Fragment1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View = inflater.inflate(R.layout.fragment_1, container, false)
        view.findViewById<Button>(R.id.logout).setOnClickListener {
            val editor = this.activity?.getSharedPreferences("memory", Context.MODE_PRIVATE)?.edit()
            editor?.putString("token", "")
            editor?.apply()
            val intent : Intent = Intent(this.activity, LoginActivity::class.java)
            this.activity?.startActivity(intent)
            this.activity?.overridePendingTransition(0, 0)
            this.activity?.finish()
        }
        return view
    }
}