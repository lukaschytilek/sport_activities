package cz.chytilek.sportactivities.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.widget.*
import com.google.firebase.database.*
import cz.chytilek.sportactivities.MainActivity
import cz.chytilek.sportactivities.R
import cz.chytilek.sportactivities.model.SportActivity
import cz.chytilek.sportactivities.provider.SportActivityMeta

class ListOfSportActivitiesFragment : Fragment(), AdapterView.OnItemLongClickListener,
    ValueEventListener, AdapterView.OnItemClickListener {

    private var mListActivities: MutableList<SportActivity> = mutableListOf()
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var mAdapter: SportAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list_of_sport_activities, container, false)
        val list = root.findViewById<ListView>(R.id.list)
        mAdapter = SportAdapter(context, data = mListActivities)
        list.adapter = mAdapter

        list.onItemLongClickListener = this
        list.onItemClickListener = this

        firebaseDatabase = FirebaseDatabase.getInstance().getReference(
            SportActivityMeta.CHILD_KEY)

        return root
    }

    override fun onResume() {
        super.onResume()

        mListActivities = SportActivityMeta.getAllSportActivity(context!!)
        mAdapter.updateData(mListActivities)

        firebaseDatabase.addValueEventListener(this)
    }

    override fun onPause() {
        super.onPause()

        firebaseDatabase.removeEventListener(this)
    }

    private fun findAndRemoveFBDataFromList(){
        val newList: MutableList<SportActivity> = mutableListOf()
        for(sport in mListActivities){
            if(sport.DBType == 0)
                newList.add(sport)
        }

        mListActivities = newList
    }

    override fun onCancelled(p0: DatabaseError) {
        Toast.makeText(context, R.string.failed_read_firebase, Toast.LENGTH_SHORT).show()
    }

    override fun onDataChange(p0: DataSnapshot) {
        val dataDB = p0.value
        findAndRemoveFBDataFromList()
        if(dataDB is Map<*, *>){
            for(key in dataDB.keys){
                val sport = dataDB[key] as Map<*, *>
                mListActivities.add(SportActivity(-1, sport["name"] as String, sport["location"] as String, sport["lengthOfActivity"] as Long, (sport["DBType"] as Long).toInt()))
            }
        }
        mAdapter.updateData(mListActivities)
    }

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long): Boolean {
        val sportActivity = mListActivities[p2]

        context?.let {
            val dialog = AlertDialog.Builder(it)
            dialog.setTitle(R.string.delete)
            dialog.setMessage(getString(R.string.delete_record, sportActivity.name))
            dialog.setNegativeButton(android.R.string.no) { dialogInterface, _ -> dialogInterface.dismiss() }
            dialog.setPositiveButton(android.R.string.yes) { dialogInterface, _ ->
                if(sportActivity.DBType == 0) {
                    SportActivityMeta.delete(it, sportActivity)
                }
                if(sportActivity.DBType == 1) {
                    SportActivityMeta.deleteFB(sportActivity)
                }
                mListActivities.removeAt(p2)
                mAdapter.updateData(mListActivities)
                dialogInterface.dismiss()
            }

            dialog.show()
        }

        return true
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val sportActivity = mListActivities[p2]
        activity?.let {
            if(it is MainActivity){
                it.openAddFragmentWithBundle(sportActivity)
            }
        }
    }

    class SportAdapter(context: Context?, private var data: MutableList<SportActivity>) : BaseAdapter() {

        private var mLayoutInflater: LayoutInflater? = null

        init {
            mLayoutInflater = LayoutInflater.from(context)
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
            val convertView: View?
            if(p1 == null){
                convertView = mLayoutInflater?.inflate(R.layout.list_item, p2, false)
            } else
                convertView = p1

            val sportActivity = getItem(p0)

            val color: Int?
            if(sportActivity.DBType == 0)
                color = Color.RED
            else
                color = Color.BLUE

            convertView?.findViewById<TextView>(R.id.sport_name)?.text = sportActivity.name
            convertView?.findViewById<TextView>(R.id.sport_location)?.text = sportActivity.location
            convertView?.findViewById<TextView>(R.id.sport_duration)?.text = String.format("%d min", sportActivity.lengthOfActivity)

            convertView?.findViewById<TextView>(R.id.sport_name)?.setTextColor(color)
            convertView?.findViewById<TextView>(R.id.sport_location)?.setTextColor(color)
            convertView?.findViewById<TextView>(R.id.sport_duration)?.setTextColor(color)

            return convertView
        }

        override fun getItem(p0: Int): SportActivity {
            return data[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return data.size
        }

        fun updateData(data: MutableList<SportActivity>){
            this.data = data
            this.notifyDataSetChanged()
        }
    }
}
