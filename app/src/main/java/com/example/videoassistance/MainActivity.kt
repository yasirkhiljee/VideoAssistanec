 package com.example.videoassistance

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


 class MainActivity : AppCompatActivity() {
    companion object {
        private lateinit var videoView: VideoView
    }


    private val mList : MutableList<QuestionsAnswers>  = ArrayList()
    val mQuestionsAnswers = QuestionsAnswers("The course objectives were clear?", "")
    val mQuestionsAnswers2 = QuestionsAnswers("Quality of Video was Clear?", "")
    val mQuestionsAnswers3 = QuestionsAnswers("Topic Was Clearly Described?", "")
    val mQuestionsAnswers4 = QuestionsAnswers("The Course workload was manageable?", "")
    val mQuestionsAnswers5 = QuestionsAnswers("The Course was well organized?", "")
    val mQuestionsAnswers6 = QuestionsAnswers("Interaction with the students was done?", "")
    val mQuestionsAnswers7 = QuestionsAnswers("Complete Course watched by the Student?", "")
    private lateinit var mediaController: MediaController
     private var filePath: File? = null

    private var shownDialog = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }
private lateinit var mMediaPlayer : MediaPlayer
    private fun init() {
        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            videoView = findViewById<VideoView>(R.id.view_Video)
            val uri: Uri =
                    Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_version)
            // if data comes from server then call this
//            selectedCarVideo.setVideoPath("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4")
            //if data comes from raw folder then call this
            videoView.setVideoURI(uri)
            videoView.requestFocus()
            mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)
            videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->
                mMediaPlayer = mediaPlayer
                mediaPlayer.isLooping = false
                try{
                val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
                val screenRatio  = videoView.width / videoView.height
                val scale = videoRatio / screenRatio
                if (scale >= 1f) {
                    videoView.scaleX = scale
                } else {
                    videoView.scaleY = 1f / scale
                }
                }catch (ex:Exception){ex.printStackTrace()}
                runnable()
            })

            videoView.setOnCompletionListener { mp -> // not playVideo
               showFeedBackDialog()
            }

            videoView.start()

            filePath = File(getExternalFilesDir(null), "Feedback.docx")
            ActivityCompat.requestPermissions(
                    this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
                    PackageManager.PERMISSION_GRANTED
            )
            try {
                if (!filePath!!.exists()) {
                    filePath!!.createNewFile()
                }
//                showFeedBackDialog()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun runnable() {
        var handler = Handler()

        val r: Runnable = object : Runnable {
            @SuppressLint("DefaultLocale")
            override fun run() {
                try {
                    if (videoView.isPlaying) {
                        val duration: Long = videoView.currentPosition.toLong()
                        val time = java.lang.String.format("%02d-%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))
                        if (time == "00-17"){
                            if (shownDialog == 0) {
                                shownDialog = 1
                                videoView.pause()
                                showQuestionDialog(1)
                            }
                        }
                        else if (time == "01-16"){
                            if (shownDialog == 1) {
                                shownDialog = 2
                                videoView.pause()
                                showQuestionDialog(2)
                            }
                        }
                        else if (time == "02-25"){
                            if (shownDialog == 2) {
                                shownDialog = 3
                                videoView.pause()
                                showQuestionDialog(3)
                            }
                        }
                        else if (time == "05-22"){
                            if (shownDialog == 3) {
                                shownDialog = 4
                                videoView.pause()
                                showQuestionDialog(4)
                            }
                        }
                        else if (time == "06-58"){
                            if (shownDialog == 4) {
                                shownDialog = 5
                                videoView.pause()
                                showQuestionDialog(5)
                            }
                        }
                        Log.e("Main", "$time")
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                handler.postDelayed(this, 1000)
            }
        }

        handler.postDelayed(r, 1000)
    }

    override fun onPause() {
        super.onPause()
        try {
            videoView.pause()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            videoView.resume()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

fun showQuestionDialog(questionNo: Int){
    val view: View = layoutInflater.inflate(R.layout.question_layout, null)
    val dialog = BottomSheetDialog(this)
    dialog.setContentView(view)
    dialog.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)

    val mQuestion: TextView = view.findViewById(R.id.question)
    val mEdittext: TextInputEditText = view.findViewById(R.id.questionAnswer)
    val mSubmitBtn: MaterialButton = view.findViewById(R.id.submitBtn)

    if (questionNo == 1){
        mQuestion.text = "What do you mean by Online Exam?"
    }
    else if (questionNo == 2){
        mQuestion.text = "What is a Email Address?"
    }
    else if (questionNo == 3){
        mQuestion.text = "TCExam is an application to conduct an online Exam?"
    }
    else if (questionNo == 4){
        mQuestion.text = "Are You Watching the Lecture?"
    }
    else if (questionNo == 5){
        mQuestion.text = "Are You Satisfied with the Lecture?"
    }
    mSubmitBtn.setOnClickListener {
        if (mEdittext.text!!.isEmpty()){
            mEdittext.error = "this filed is required"
        }
        else {
            dialog.dismiss()
            videoView.start()
        }
    }

    dialog.show()
}

    private lateinit var mQuestion :TextView
var feedBackQuestion = 1
    fun showFeedBackDialog() {
        val view: View = layoutInflater.inflate(R.layout.question_layout, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

         mQuestion = view.findViewById(R.id.question)
        val mEdittext: TextInputEditText = view.findViewById(R.id.questionAnswer)
        val mSubmitBtn: MaterialButton = view.findViewById(R.id.submitBtn)
        mList.add(mQuestionsAnswers)
        mList.add(mQuestionsAnswers2)
        mList.add(mQuestionsAnswers3)
        mList.add(mQuestionsAnswers4)
        mList.add(mQuestionsAnswers5)
        mList.add(mQuestionsAnswers6)
        mList.add(mQuestionsAnswers7)
        updateQuestion()
        mSubmitBtn.setOnClickListener {
            if (mEdittext.text!!.isEmpty()){
                mEdittext.error = "this filed is required"
            }
            else {
                if (feedBackQuestion == 1){
                    mQuestionsAnswers.answer = mEdittext.text.toString()
                }
                else if (feedBackQuestion == 2){
                    mQuestionsAnswers2.answer = mEdittext.text.toString()
                }
                else if (feedBackQuestion == 3){
                    mQuestionsAnswers3.answer = mEdittext.text.toString()
                }
                else if (feedBackQuestion == 4){
                    mQuestionsAnswers4.answer = mEdittext.text.toString()
                }
                else if (feedBackQuestion == 5){
                    mQuestionsAnswers5.answer = mEdittext.text.toString()
                }
                else if (feedBackQuestion == 6){
                    mQuestionsAnswers6.answer = mEdittext.text.toString()
                }
                if (feedBackQuestion < 7) {
                    feedBackQuestion++
                    mEdittext.setText("")
                    updateQuestion()
                }
            }
        }

        dialog.show()
    }

    private fun updateQuestion() {
        if (feedBackQuestion == 1){
            mQuestion.text = "The course objectives were clear?"
        }
        else if (feedBackQuestion == 2){
            mQuestion.text = "Quality of Video was Clear?"
        }
        else if (feedBackQuestion == 3){
            mQuestion.text = "Topic Was Clearly Described?"
        }
        else if (feedBackQuestion == 4){
            mQuestion.text = "The Course workload was manageable?"
        }
        else if (feedBackQuestion == 5){
            mQuestion.text = "The Course was well organized?"
        }
        else if (feedBackQuestion == 6){
            mQuestion.text = "Interaction with the students was done?"
        }
        else if (feedBackQuestion == 7){
            mQuestion.text = "Complete Course watched by the Student?"
            mQuestionsAnswers7.answer = "yes"
            createDocument(mList)
//            finish()
        }
    }

    private fun createDocument(mList: MutableList<QuestionsAnswers>) {
        var szText = "Video Assistance\n"
        for (i in mList.indices){
            szText += "${(i+1)} ${mList[i].question} : ${mList[i].answer} \n"
        }

        buttonCreate(szText)

    }

     fun buttonCreate(szText: String) {
         try {
             val xwpfDocument = XWPFDocument()
             val xwpfParagraph = xwpfDocument.createParagraph()
             val xwpfRun = xwpfParagraph.createRun()
             xwpfRun.setText(szText)
             xwpfRun.fontSize = 24
             val fileOutputStream = FileOutputStream(filePath)
             xwpfDocument.write(fileOutputStream)
             if (fileOutputStream != null) {
                 fileOutputStream.flush()
                 fileOutputStream.close()
             }
             xwpfDocument.close()
             Toast.makeText(this, "save file on storage", Toast.LENGTH_SHORT).show()
             finish()
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

    var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }



}