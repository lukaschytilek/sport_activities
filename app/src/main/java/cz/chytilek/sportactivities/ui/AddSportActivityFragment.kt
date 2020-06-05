package cz.chytilek.sportactivities.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.widget.*
import cz.chytilek.sportactivities.R
import cz.chytilek.sportactivities.model.SportActivity
import cz.chytilek.sportactivities.provider.SportActivityMeta

class AddSportActivityFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var mSportActivity: SportActivity? = null
    private var isNew: Boolean = true
    private lateinit var mNameEditText: EditText
    private lateinit var mLocationEditText: EditText
    private lateinit var mDurationEditText: EditText
    private lateinit var mDBTypeSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_add_sport_activity, container, false)
        mNameEditText = root.findViewById(R.id.sport_name)
        mLocationEditText = root.findViewById(R.id.sport_location)
        mDurationEditText = root.findViewById(R.id.sport_duration)
        mDBTypeSpinner = root.findViewById(R.id.db_spinner)
        mDBTypeSpinner.onItemSelectedListener = this

        val dataAdapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, arrayOf("SQLite", "Firebase")) }
        dataAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDBTypeSpinner.adapter = dataAdapter

        root.findViewById<Button>(R.id.sport_add_btn).setOnClickListener(this)

        mSportActivity = null

        arguments?.let {
            if(it.containsKey(SportActivityMeta.BUNDLE_KEY)){
                mSportActivity = it.getSerializable(SportActivityMeta.BUNDLE_KEY) as SportActivity

                mSportActivity?.let { sportActivity ->
                    mNameEditText.setText(sportActivity.name)
                    mLocationEditText.setText(sportActivity.location)
                    mDurationEditText.setText(sportActivity.lengthOfActivity.toString())
                    mDBTypeSpinner.setSelection(sportActivity.DBType)

                    mDBTypeSpinner.isEnabled = false
                    isNew = false
                }

            }
        }

        return root
    }

    private fun getDBType() : Int{
        val selectedString = mDBTypeSpinner.selectedItem as String
        return if(selectedString.equals("SQLite", false))
            0
        else
            1
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.sport_add_btn -> {
                if(TextUtils.isEmpty(mNameEditText.text)) {
                    Toast.makeText(context, R.string.fill_name, Toast.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(mLocationEditText.text)) {
                    Toast.makeText(context, R.string.fill_location, Toast.LENGTH_SHORT).show()
                    return
                }
                if(TextUtils.isEmpty(mDurationEditText.text)) {
                    Toast.makeText(context, R.string.fill_duration, Toast.LENGTH_SHORT).show()
                    return
                }

                var activityId: Long = -1
                var originalName: String? = null
                mSportActivity?.let {sportActivity ->
                    activityId = sportActivity.ID
                    originalName = sportActivity.name
                }

                mSportActivity = SportActivity(
                    activityId,
                    mNameEditText.text.toString(),
                    mLocationEditText.text.toString(),
                    mDurationEditText.text.toString().toLong(),
                    getDBType()
                )

                mSportActivity?.let {sportActivity ->
                    context?.let {
                        if (sportActivity.DBType == 0) {
                            if(isNew) {
                                if(SportActivityMeta.getSportActivityByName(it, sportActivity.name) != null){
                                    Toast.makeText(context, R.string.name_exists, Toast.LENGTH_SHORT).show()
                                    return
                                }
                            }
                            SportActivityMeta.insert(it, sportActivity)
                        }
                        if (sportActivity.DBType == 1) {
                            originalName?.let { it1 -> SportActivityMeta.deleteFB(it1) }
                            SportActivityMeta.insertFB(sportActivity)
                        }
                        Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show()

                        activity?.onBackPressed()
                    }
                }
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }
}
