package com.example.android.roomwordssample.contacts

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.android.roomwordssample.databinding.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

private const val IMAGE_VIEW_TYPE = 0

class FormAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var usr: User =
        User(null,"","","","","","",null,0.0,"",null,0.0,0.0)
    var highlight: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("TAG", "onCreateViewHolder: ")
        return when(viewType){
            IMAGE_VIEW_TYPE -> ImageViewHolder.create(parent)
            4 -> MobileViewHolder.create(parent)
            5 -> EmailViewHolder.create(parent)
            6 -> GenderViewHolder.create(parent)
            7 -> DobViewHolder.create(parent)
            8 -> MultipleSelectViewHolder.create(parent)
            9 -> IncomeViewHolder.create(parent)
            else -> TextViewHolder.create(parent)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            0 -> (holder as ImageViewHolder).bind(usr.image?.let {
                DatabaseConverters().bytetoBitmap(
                    it
                )
            },holder.itemView.context,highlight==0)
            1 -> (holder as TextViewHolder).bind(usr,1,highlight==1)
            2 -> (holder as TextViewHolder).bind(usr,2,highlight==2)
            3 -> (holder as TextViewHolder).bind(usr,3,highlight==3)
            4 -> (holder as MobileViewHolder).bind(usr,highlight==4)
            5 -> (holder as EmailViewHolder).bind(usr,highlight==5)
            6 -> (holder as GenderViewHolder).bind(usr,highlight==6)
            7 -> (holder as DobViewHolder).bind(usr,holder.itemView.context,highlight==7)
            8 -> (holder as MultipleSelectViewHolder).bind(usr,holder.itemView.context,highlight==8)
            9 -> (holder as IncomeViewHolder).bind(usr,highlight==9)
            else -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 10
    }

}

private class DobViewHolder(var binding: DobButtonItemBinding) : RecyclerView.ViewHolder(binding.root) {
    var cal: Calendar = Calendar.getInstance()

    fun bind(usr: User, context: Context, highlight: Boolean){
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        if(usr.dob!=null){
        binding.BTNDATE.text = sdf.format(usr.dob?.time ?: "")
            cal.set(usr.dob!!.year, usr.dob!!.month, usr.dob!!.date)
        }

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.warningText.visibility = View.VISIBLE
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.warningText.visibility = View.GONE
        }


        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                usr.dob = cal.time
                binding.BTNDATE.text = sdf.format(cal.getTime())
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.warningText.visibility = View.GONE
            }
        }

        binding.BTNDATE.setOnClickListener {
            val dialog = DatePickerDialog(context,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.maxDate = Calendar.getInstance().time.time
            dialog.show()
        }
    }

    companion object{
        fun create(parent: ViewGroup) = DobViewHolder(DobButtonItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

private class EmailViewHolder(var binding: EmailItemBinding) : RecyclerView.ViewHolder(binding.root) {

    private fun validEmail(): String? {
        val passwordText = binding.editEmail.text.toString()
        if(passwordText.length < 1) {
            return "Email Should Not be Empty"
        }

        if(!passwordText.contains(".*@.*".toRegex())) {
            return "Not a valid email"
        }

        return null
    }

    fun bind(usr: User, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.loginEmailContainer.helperText = validEmail()
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.editEmail.setText(usr.email)

        binding.editEmail.addTextChangedListener {
            binding.loginEmailContainer.helperText = validEmail()
            if(validEmail()==null){
                usr.email = binding.editEmail.text.toString()
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }else{
                usr.email = ""
            }
        }
    }
    companion object{
        fun create(parent: ViewGroup) = EmailViewHolder(EmailItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

private class GenderViewHolder(var binding: RadioItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(usr: User, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.warningText.visibility=View.VISIBLE
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.warningText.visibility=View.GONE
        }

        if(usr.gender != "")
            binding.Gender.check(when(usr.gender){
                "Male" -> binding.Male.id
                "Female" -> binding.Female.id
                else -> binding.Other.id
            })
        binding.Gender.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                usr.gender = when(checkedId){
                    binding.Male.id -> "Male"
                    binding.Female.id -> "Female"
                    binding.Other.id -> "Other"
                    else -> ""
                }
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.warningText.visibility=View.GONE
            }
        })
    }

    companion object{
        fun create(parent: ViewGroup) = GenderViewHolder(RadioItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

class ImageViewHolder(var binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root) {

    interface SendImagePickReuest{
        fun req()
    }

    fun bind(image: Bitmap?, context: Context, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.warningText.visibility=View.VISIBLE
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.warningText.visibility=View.GONE
        }

        if(image!=null) {
            binding.TVUpImg.visibility = View.GONE
            binding.ImageProfile.setImageBitmap(image)
        }
        binding.UploadLay.setOnClickListener {
            var x = (context as UserFormActivity).req()
        }
    }


    companion object{
        fun create(parent: ViewGroup) = ImageViewHolder(ImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

private class IncomeViewHolder(var binding: IncomeItemBinding) : RecyclerView.ViewHolder(binding.root) {

    class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) :
        InputFilter {
        var mPattern: Pattern
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val matcher: Matcher = mPattern.matcher(dest)
            return if (!matcher.matches()) "" else null
        }

        init {
            mPattern =
                Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
        }
    }

    private fun validPrice(): String? {
        val passwordText = binding.editPrice.text.toString()
        if(passwordText.length < 1) {
            return "Price Should Not be zero"
        }

        if(passwordText.length > 1000000) {
            return "Price Should be less than 10 Lacks"
        }

        return null
    }

    fun bind(usr: User, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.loginPriceContainer.helperText = validPrice()
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.editPrice.setText(usr.income.toString())

        binding.editPrice.addTextChangedListener {
            binding.loginPriceContainer.helperText = validPrice()
            if(validPrice()==null){
                usr.income = binding.editPrice.text.toString().toDouble()
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }else{
                usr.income = 0.0
            }
        }

        binding.editPrice.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(8, 2)))
    }
    companion object{
        fun create(parent: ViewGroup) = IncomeViewHolder(IncomeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

private class MobileViewHolder(var binding: InputMobileBinding) : RecyclerView.ViewHolder(binding.root) {

    fun validMobile(): String? {
        val passwordText = binding.editText.text.toString()
        if(passwordText.length != 10) {
            return "Not a Valid Number"
        }

        return null
    }


    fun bind(usr: User, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.TextContainer.helperText = validMobile()
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.editText.setText(usr.mob)

        binding.editText.addTextChangedListener {
            binding.TextContainer.helperText = validMobile()
            if(validMobile()==null){
                usr.mob = binding.editText.text.toString()
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }else{
                usr.mob = ""
            }
        }
    }
    companion object{
        fun create(parent: ViewGroup) = MobileViewHolder(InputMobileBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

public class MultipleSelectViewHolder(var binding: SelectMultipleItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(usr: User, context: Context, highlight: Boolean){

        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.warningText.visibility  = View.VISIBLE
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.warningText.visibility  = View.GONE
        }

        if(usr.industry!=""){
            binding.textView.setText(usr.industry)
        }

        val arrayColors = arrayOf("IT","Health","pharmaceutical","Fintech")


        var arrayChecked = booleanArrayOf(usr.industry.contains("IT"),usr.industry.contains("Health"),usr.industry.contains("pharmaceutical"),usr.industry.contains("Fintech"))


        binding.textView.setOnClickListener {
            var builder = AlertDialog.Builder(context)
            builder.setTitle("Set Industry")

            builder.setMultiChoiceItems(arrayColors, arrayChecked) { dialog, which, isChecked ->
                // Update the clicked item checked status
                arrayChecked[which] = isChecked
            }

            builder.setNegativeButton(
                "Cancel"
            ) {
                    dialogInterface, i -> // dismiss dialog
                dialogInterface.dismiss()
            }

            builder.setPositiveButton("OK") { _, _ ->
                // Do something when click positive button
                binding.textView.setText("")

                for (i in 0 until arrayColors.size) {
                    val checked = arrayChecked[i]
                    if (checked) {
                        binding.textView.setText( "${binding.textView.text}${arrayColors[i]}\n")
                    }
                }
                binding.textView.setText( binding.textView.text.toString().trim())
                usr.industry = binding.textView.text.toString()
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
                binding.warningText.visibility  = View.GONE
            }



            // Initialize the AlertDialog using builder object
            var dialog = builder.setCancelable(false).create()

            // Finally, display the alert dialog
            dialog.show()

        }
    }
    companion object{
        fun create(parent: ViewGroup) = MultipleSelectViewHolder(SelectMultipleItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}

private class TextViewHolder(var binding: InputItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun validate() : String?{
        val passwordText = binding.editText.text.toString()
        if(passwordText.length < 1) {
            return "Field Should Not be Empty"
        }

        if(!passwordText.matches(".*[a-zA-Z].*".toRegex())) {
            return "Must Contain 1 Alphabet Character"
        }

        return null
    }

    fun bind(user: User, type: Int, highlight: Boolean){



        if(highlight){
            binding.root.setBackgroundColor(Color.parseColor("#b5d4e6"))
            binding.TextContainer.helperText = validate()
        }else{
            binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        binding.editText.setText(when(type){
            1 -> user.Fname
            2 -> user.Lname
            else -> user.Address
        })
        binding.hint = when(type){
            1 -> "First Name"
            2 -> "Last Name"
            else -> "Address"
        }

        binding.editText.addTextChangedListener {
            binding.TextContainer.helperText = validate()
            if(validate()==null){
                when(type){
                    1 -> user.Fname = binding.editText.text.toString()
                    2 -> user.Lname = binding.editText.text.toString()
                    else -> user.Address = binding.editText.text.toString()
                }
                binding.root.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }else{
                when(type){
                    1 -> user.Fname = ""
                    2 -> user.Lname = ""
                    else -> user.Address = ""
                }
            }
        }
    }
    companion object{
        fun create(parent: ViewGroup) = TextViewHolder(InputItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
}