package com.example.feemanagementsystem.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.feemanagementsystem.R
import com.example.feemanagementsystem.databinding.FragmentAddMemberBinding
import com.example.feemanagementsystem.databinding.RenewDialogBinding
import com.example.feemanagementsystem.global.CaptureImage
import com.example.feemanagementsystem.global.DB
import com.example.feemanagementsystem.global.MyFunction
import java.text.SimpleDateFormat
import java.util.*



@Suppress("DEPRECATION")
class FragmentAddMember : Fragment() {

    var db: DB? = null
    var oneMonth: String? = ""
    var sixMonths: String? = ""
    var nineMonths: String? = ""
    var threeMonths: String? = ""
    var twelveMonths: String? = ""
    lateinit var binding: FragmentAddMemberBinding
    private lateinit var bindingDialog: RenewDialogBinding
    private var captureImage: CaptureImage? = null
    private val REQUEST_CAMERA = 1234
    private val REQUEST_GALLERY = 5464
    private var actualImagePath = ""
    private var gender = "Male"
    private var ID = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Add New Student"
        db = activity?.let { DB(it) }
        captureImage = CaptureImage(activity)
        ID = arguments!!.getString("ID").toString()
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.edtJoining.setText(sdf.format(cal.time))

            }

        binding.spMonthly.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                p2: Int,
                p4: Long,
            ) {
                val value = binding.spMonthly.selectedItem.toString().trim()
                if (value == "Select") {
                    binding.edtRepayment.setText("")
                    calculateTotal(binding.spMonthly, binding.edtDiscount, binding.edtAmount)
                } else {
                    if (binding.edtJoining.text.toString().trim().isNotEmpty()) {
                        if (value == "1 Month") {
                            calculatedExpireDate(1, binding.edtJoining, binding.edtRepayment)
                            calculateTotal(
                                binding.spMonthly,
                                binding.edtDiscount,
                                binding.edtAmount
                            )
                        } else if (value == "3 Months") {
                            calculatedExpireDate(3, binding.edtJoining, binding.edtRepayment)
                            calculateTotal(
                                binding.spMonthly,
                                binding.edtDiscount,
                                binding.edtAmount
                            )
                        } else if (value == "6 Months") {
                            calculatedExpireDate(6, binding.edtJoining, binding.edtRepayment)
                            calculateTotal(
                                binding.spMonthly,
                                binding.edtDiscount,
                                binding.edtAmount
                            )
                        } else if (value == "9 Months") {
                            calculatedExpireDate(9, binding.edtJoining, binding.edtRepayment)
                            calculateTotal(
                                binding.spMonthly,
                                binding.edtDiscount,
                                binding.edtAmount
                            )
                        } else if (value == "12 Months") {
                            calculatedExpireDate(12, binding.edtJoining, binding.edtRepayment)
                            calculateTotal(
                                binding.spMonthly,
                                binding.edtDiscount,
                                binding.edtAmount
                            )
                        }
                    } else {
                        showToast("Select Monthly Payment first")
                        binding.spMonthly.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.edtDiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    calculateTotal(binding.spMonthly, binding.edtDiscount, binding.edtAmount)
                }
            }

        })
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                R.id.rdMale -> {
                    gender = "Male"
                }
                R.id.rdFeMale -> {
                    gender = "Female"
                }
            }
        }
        binding.btnSave.setOnClickListener {
            if (validate()) {
                saveDate(requireContext())
            }
        }
        binding.btnSave.setOnClickListener {
            if (validate()) {
                saveDate(requireContext() as Context)
            }
        }

        binding.imgPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
        binding.imgTakeImage.setOnClickListener {
            getImage()
        }
        getFee()
        binding.btnActiveInactive.setOnClickListener {
            try {
                if (getStatus() == "A") {
                    val sqlQuery = "UPDATE MEMBER SET STATUS='D' WHERE ID='$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member is Inactive now")
                } else {
                    val sqlQuery = "UPDATE MEMBER SET STATUS='A' WHERE ID='$ID'"
                    db?.fireQuery(sqlQuery)
                    showToast("Member is Active now")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (ID.trim().isNotEmpty()) {
            if (getStatus() == "A") {
                binding.btnActiveInactive.text = "Inactive"
                binding.btnActiveInactive.visibility = View.VISIBLE
            } else {
                binding.btnActiveInactive.text = "Active"
                binding.btnActiveInactive.visibility = View.VISIBLE
            }
            loadData()
        } else {

            binding.btnActiveInactive.visibility = View.GONE
        }
        binding.btnRenewalSave.setOnClickListener {
            if (ID.trim().isNotEmpty()) {
                openRenewalDialog()
            }
        }
    }

    private fun getStatus(): String {
        var status = ""
        try {
            val sqlQuery = "SELECT STATUS FROM MEMBER WHERE ID='$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    status = MyFunction.getValue(it, "STATUS")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return status
    }

    private fun getFee() {
        try {
            val sqlQueue = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQueue)?.use {

                if (it.count > 0) {
                    oneMonth = MyFunction.getValue(it, "ONE_MONTH")
                    threeMonths = MyFunction.getValue(it, "THREE_MONTH")
                    sixMonths = MyFunction.getValue(it, "SIX_MONTH")
                    nineMonths = MyFunction.getValue(it, "NINE_MONTH")
                    twelveMonths = MyFunction.getValue(it, "TWELVE_MONTH")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun calculateTotal(spMember: Spinner, edtDis: EditText, edtAmt: EditText) {
        val month = spMember.selectedItem.toString().trim()
        var discount = edtDis.text.toString().trim()
        if (edtDis.text.toString().isEmpty()) {
            discount = "0"
        }
        if (month == "Select") {
            edtAmt.setText("")
        } else if (month == "1 Month") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (oneMonth!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((oneMonth!!.toDouble() * discount.toDouble()) / 100) //finding out the discount amount
                val total =
                    oneMonth!!.toDouble() - discountAmount //Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))

            }
        } else if (month == "3 Months") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (threeMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((threeMonths!!.toDouble() * discount.toDouble()) / 100) //finding out the discount amount
                val total =
                    threeMonths!!.toDouble() - discountAmount //Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }
        } else if (month == "6 Months") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (sixMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((sixMonths!!.toDouble() * discount.toDouble()) / 100) //finding out the discount amount
                val total =
                    sixMonths!!.toDouble() - discountAmount //Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }
        } else if (month == "9 Months") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (nineMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((nineMonths!!.toDouble() * discount.toDouble()) / 100) //finding out the discount amount
                val total =
                    nineMonths!!.toDouble() - discountAmount //Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }
        } else if (month == "12 Months") {
            if (discount.trim().isEmpty()) {
                discount = "0"
            }
            if (twelveMonths!!.trim().isNotEmpty()) {
                val discountAmount =
                    ((twelveMonths!!.toDouble() * discount.toDouble()) / 100) //finding out the discount amount
                val total =
                    twelveMonths!!.toDouble() - discountAmount //Minus discount amount from main amount
                edtAmt.setText(String.format("%.2f", total))
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculatedExpireDate(month: Int, edtJoining: EditText, editExpiry: EditText) {
        val dtStart = edtJoining.text.toString().trim()
        if (dtStart.isNotEmpty()) {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date1 = format.parse(dtStart)
            val cal = Calendar.getInstance()
            cal.time = date1
            cal.add(Calendar.MONTH, month)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)

            binding.edtRepayment.setText(sdf.format(cal.time))
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }


    private fun getImage() {
        val items: Array<CharSequence>
        try {
            items = arrayOf("Take Photo", "Choose Image", "Cancel")
            val builder = android.app.AlertDialog.Builder(activity)
            builder.setCancelable(false)
            builder.setTitle("Select Image")
            builder.setItems(items) { dialogInterface, i ->
                if (items[i] == "Take Photo") {
                    com.github.florent37.runtimepermission.RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.CAMERA)
                        .onAccepted {
                            takePicture()
                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Please accept our permission to capture image")
                                .setPositiveButton("Yes") { dialogInterface, i ->
                                    it.askAgain()
                                }
                                .setNegativeButton("No") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                }
                                .show()
                        }
                        .ask()


                } else if (items[i] == "Choose Image") {
                    com.github.florent37.runtimepermission.RuntimePermission.askPermission(this)
                        .request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .onAccepted {
                            takeFromGallery()
                        }
                        .onDenied {
                            android.app.AlertDialog.Builder(activity)
                                .setMessage("Please accept our permission to capture image")
                                .setPositiveButton("Yes") { dialogInterface, i ->
                                    it.askAgain()
                                }
                                .setNegativeButton("No") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                }
                                .show()
                        }
                        .ask()
                } else {
                    dialogInterface.dismiss()
                }
            }
            builder.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureImage?.setImageUri())
        takePicIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(takePicIntent, REQUEST_CAMERA)
    }

    private fun takeFromGallery() {
        val intent = Intent()
        intent.type = "image/jpg"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            captureImage(captureImage?.getRightAngleImage(captureImage?.imagePath).toString())
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            captureImage(
                captureImage?.getRightAngleImage(
                    captureImage?.getPath(
                        data?.data,
                        context
                    )
                ).toString()
            )
        }
    }

    private fun captureImage(path: String) {
        Log.d("FragmentAdd", "imagePath : $path")
        getImagePath(captureImage?.decodeFile(path))
    }

    private fun getImagePath(bitmap: Bitmap?) {
        val tempUri: Uri? = captureImage?.getImageUri(activity, bitmap)
        actualImagePath = captureImage?.getRealPathFromURI(tempUri, activity).toString()
        Log.d("FragmentAdd", "ActualImagePath : $actualImagePath")

        activity?.let {
            Glide.with(it)
                .load(actualImagePath)
                .into(binding.imgPic)
        }
    }

    private fun validate(): Boolean {
        if (binding.edtFirstName.text.toString().trim().isEmpty()) {
            showToast("Enter first name")
            return false
        } else if (binding.edtLastName.text.toString().trim().isEmpty()) {
            showToast("Enter last name")
            return false
        } else if (binding.edtClass.text.toString().trim().isEmpty()) {
            showToast("Enter Class")
            return false
        } else if (binding.edtMobile.text.toString().trim().isEmpty()) {
            showToast("Enter mobile number")
            return false
        }
        return true
    }

    private fun saveDate(context: Context) {
        try {
            var myIncreamentId = ""
            if (ID.trim().isEmpty()) {
                myIncreamentId = getIncrementedId()
            } else {
                myIncreamentId = ID
            }
            val sqlQuery =
                "INSERT OR REPLACE INTO MEMBER(ID, FIRST_NAME, LAST_NAME, GENDER, CLASS," +
                        "REGISTER_NUMBER, MOBILE, SECTION, DATE_OF_JOINING, MONTHLY, REPAYMENT, DISCOUNT, TOTAL, IMAGE_PATH, STATUS) VALUES" +
                        "('" + myIncreamentId + "', " + DatabaseUtils.sqlEscapeString(
                    binding.edtFirstName.text.toString().trim()
                ) + "," +
                        "" + DatabaseUtils.sqlEscapeString(
                    binding.edtLastName.text.toString().trim()
                ) + ", '" + gender + "'," +
                        "'" + binding.edtClass.text.toString()
                    .trim() + "','" + binding.edtReg.text.toString().trim() + "'," +
                        "" + binding.edtMobile.text.toString()
                    .trim() + ", " + DatabaseUtils.sqlEscapeString(
                    binding.edtSection.text.toString().trim()
                ) + "," +
                        "'" + MyFunction.returnUserDateFormat(
                    binding.edtJoining.text.toString().trim()
                ) + "','" + binding.spMonthly.selectedItem.toString().trim() + "'," +
                        "'" + MyFunction.returnUserDateFormat(
                    binding.edtRepayment.text.toString().trim()
                ) + "','" + binding.edtDiscount.text.toString().trim() + "'," +
                        "'" + binding.edtAmount.text.toString()
                    .trim() + "','" + actualImagePath + "','A')"
            db?.executeQuery(sqlQuery)

            // Schedule a job to show the toast message
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(1, ComponentName(context, MyJobService::class.java))
                .setMinimumLatency(5000) // Set a delay of 1 second before executing the job
                .build()
            jobScheduler.schedule(jobInfo)
            if (ID.trim().isEmpty()) {
                clearData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getIncrementedId(): String {
        var incrementId = ""
        try {
            val salQuery = "SELECT IFNULL(MAX(ID)+1,'1') AS ID FROM MEMBER"
            db?.fireQuery(salQuery)?.use {
                if (it.count > 0) {
                    incrementId = MyFunction.getValue(it, "ID")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return incrementId

    }

    private fun clearData() {
        binding.edtFirstName.setText("")
        binding.edtLastName.setText("")
        binding.edtClass.setText("")
        binding.edtReg.setText("")
        binding.edtMobile.setText("")
        binding.edtJoining.setText("")
        actualImagePath = ""
        Glide.with(this)
            .load(R.drawable.boy)
            .into(binding.imgPic)
    }

    @SuppressLint("SimpleDateFormat")
    private fun loadData() {
        try {
            val sqlQuery = "SELECT * FROM MEMBER WHERE ID='$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    val firstName = MyFunction.getValue(it, "FIRST NAME")
                    val lastName = MyFunction.getValue(it, "LAST_NAME")
                    val age = MyFunction.getValue(it, "CLASS")
                    val gender = MyFunction.getValue(it, "GENDER")
                    val weight = MyFunction.getValue(it, "REGISTER_NUMBER")
                    val mobileNo = MyFunction.getValue(it, "MOBILE")
                    val address = MyFunction.getValue(it, "SECTION")
                    val dateofjoin = MyFunction.getValue(it, "DATE_OF_JOINING")
                    val membership = MyFunction.getValue(it, "MONTHLY")
                    val expiry = MyFunction.getValue(it, "REPAYMENT")
                    val discount = MyFunction.getValue(it, "DISCOUNT")
                    val total = MyFunction.getValue(it, "TOTAL")
                    actualImagePath = MyFunction.getValue(it, "IMAGE_PATH")
                    binding.edtFirstName.setText(firstName)
                    binding.edtLastName.setText(lastName)
                    binding.edtClass.setText(age)
                    binding.edtReg.setText(weight)
                    binding.edtMobile.setText(mobileNo)
                    binding.edtSection.setText(address)
                    binding.edtJoining.setText(MyFunction.returnUserDateFormat(dateofjoin))
                    if (actualImagePath.isNotEmpty()) {
                        Glide.with(this)
                            .load(actualImagePath)
                            .into(binding.imgPic)
                    } else {
                        if (gender == "Male") {
                            Glide.with(this)
                                .load(R.drawable.boy)
                                .into(binding.imgPic)
                        } else {
                            Glide.with(this)
                                .load(R.drawable.girl)
                                .into(binding.imgPic)
                        }
                    }

                    if (membership.trim().isNotEmpty()) {
                        when (membership) {
                            "1 Month"-> {
                                binding.spMonthly.setSelection(1)
                            }
                            "3 Months"-> {
                                binding.spMonthly.setSelection(2)
                            }
                            "6 Months"-> {
                                binding.spMonthly.setSelection(3)
                            }
                            "9 Months"-> {
                                binding.spMonthly.setSelection(4)
                            }
                            "12 Months"-> {
                                binding.spMonthly.setSelection(5)
                            }
                            else -> {
                                binding.spMonthly.setSelection(0)
                            }
                        }
                    }
                    if (gender == "Male") {
                        binding.radioGroup.check(R.id.rdMale)
                    } else {
                        binding.radioGroup.check(R.id.rdMale)
                    }
                    binding.edtRepayment.setText(MyFunction.returnUserDateFormat(expiry))
                    binding.edtAmount.setText(MyFunction.returnUserDateFormat(total))
                    binding.edtDiscount.setText(MyFunction.returnUserDateFormat(discount))

                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val eDate = sdf.parse(expiry)
                    if (eDate!!.after(Date())) { //if expiry date greater than current date than
                        binding.btnRenewalSave.visibility = View.GONE
                    } else {
                        if (getStatus() == "A") {
                            binding.btnRenewalSave.visibility = View.VISIBLE

                        } else {

                            binding.btnRenewalSave.visibility = View.GONE

                        }
                    }

                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun openRenewalDialog() {
        bindingDialog = RenewDialogBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity!!, R.style.AlertDialogCustom)
        dialog.setContentView(bindingDialog.root)
        dialog.setCancelable(false)
        dialog.show()

        bindingDialog.edtDialogJoining.setText(binding.edtRepayment.text.toString().trim())
        bindingDialog.imgDialogRenewBack.setOnClickListener{
            dialog.dismiss()
        }

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                bindingDialog.edtDialogJoining.setText(sdf.format(cal.time))

            }
        bindingDialog.imgDialogPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
        bindingDialog.spDialogMonthly.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                p2: Int,
                p4: Long,
            ) {
                val value = bindingDialog.spDialogMonthly.selectedItem.toString().trim()
                if (value == "Select") {
                    bindingDialog.edtDialogRepayment.setText("")
                    calculateTotal(bindingDialog.spDialogMonthly ,bindingDialog.edtDialogDiscount, bindingDialog.edtDialogAmount)
                } else {
                    if (bindingDialog.edtDialogJoining.text.toString().trim().isNotEmpty()) {
                        if (value == "1 Month") {
                            calculatedExpireDate(1, bindingDialog.edtDialogJoining,bindingDialog.edtDialogRepayment)
                            calculateTotal(
                                bindingDialog.spDialogMonthly,
                                bindingDialog.edtDialogDiscount,
                                bindingDialog.edtDialogAmount
                            )
                        } else if (value == "3 Months") {
                            calculatedExpireDate(3,bindingDialog.edtDialogJoining, bindingDialog.edtDialogRepayment)
                            calculateTotal(
                                bindingDialog.spDialogMonthly,
                                bindingDialog.edtDialogDiscount,
                                bindingDialog.edtDialogAmount
                            )
                        } else if (value == "6 Months") {
                            calculatedExpireDate(6,bindingDialog.edtDialogJoining, bindingDialog.edtDialogRepayment)
                            calculateTotal(
                                bindingDialog.spDialogMonthly,
                                bindingDialog.edtDialogDiscount,
                                bindingDialog.edtDialogAmount
                            )
                        } else if (value == "9 Months") {
                            calculatedExpireDate(9,bindingDialog.edtDialogJoining, bindingDialog.edtDialogRepayment)
                            calculateTotal(
                                bindingDialog.spDialogMonthly,
                                bindingDialog.edtDialogDiscount,
                                bindingDialog.edtDialogAmount
                            )
                        } else if (value == "12 Months") {
                            calculatedExpireDate(12,bindingDialog.edtDialogJoining, bindingDialog.edtDialogRepayment)
                            calculateTotal(
                                bindingDialog.spDialogMonthly,
                                bindingDialog.edtDialogDiscount,
                                bindingDialog.edtDialogAmount
                            )
                        }
                    } else {
                        showToast("Select Monthly Payment first")
                        bindingDialog.spDialogMonthly.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        bindingDialog.edtDialogDiscount.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if(s!=null){
                    calculateTotal(
                        bindingDialog.spDialogMonthly,
                        bindingDialog.edtDialogDiscount,
                        bindingDialog.edtDialogAmount
                    )
                }
            }

        })
        bindingDialog.btnDialogRenewSave.setOnClickListener{
            if(bindingDialog.spDialogMonthly.selectedItem.toString().trim()!="Select"){
                try{
                    val sqlQuery = "UPDATE MEMBER SET DATE_OF_JOINING='"+MyFunction.returnSQLDateFormat(bindingDialog.edtDialogJoining.text.toString().trim())+"'," +
                    "MONTHLY='"+bindingDialog.spDialogMonthly.selectedItem.toString().trim()+"',"
                    "REPAYMENT='"+MyFunction.returnSQLDateFormat(bindingDialog.edtDialogRepayment.text.toString().trim())+"',"
                    "DISCOUNT='"+bindingDialog.edtDialogDiscount.text.toString().trim()+"',"
                    "TOTAL='"+bindingDialog.edtDialogAmount.text.toString().trim()+"'WHERE ID='"+ID+"'"
                    db?.executeQuery(sqlQuery)
                    showToast("Members data saved successfully")
                    dialog.dismiss()
                    loadData()
                }catch(e:Exception){
                    e.printStackTrace()
                }
            }else{
                showToast("Select Total Numbers of Month")
            }
        }

    }
}