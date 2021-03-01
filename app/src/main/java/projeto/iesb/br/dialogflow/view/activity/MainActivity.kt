package projeto.iesb.br.dialogflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import projeto.iesb.br.dialogflow.domain.Message
import projeto.iesb.br.dialogflow.domain.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

class MainActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var dialogFlow: HerokuDialogFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        retrofit = createRetrofit()
        dialogFlow= retrofit.create(HerokuDialogFlow::class.java)

    }
    fun send(){
     GlobalScope.launch(Dispatchers.Main){
         val question = etQuestion.text.toString()
         val message = Message(question, "", "123")
         val response = dialogFlow.sendMessageAsync(message)
         if (response.isNotEmpty()) {
             tvResponse.text = response[0].queryResult.fulfillmentText
             etQuestion.setText("")
         }
     }

 }
    private fun createRetrofit(): Retrofit {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().addInterceptor(logInterceptor).build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl("https://botcatador2.herokuapp.com//api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}


interface HerokuDialogFlow {

    @POST("message/text/send")
    @Headers("Content-Type: application/json")
    suspend fun sendMessageAsync(@Body message: Message): Array<Response>

}
