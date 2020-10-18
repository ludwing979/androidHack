package com.example.testapp.io;




import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface POSTService {


    //@Headers("Accept: text/html; charset=UTF-8")


    @Multipart
    @POST("acceso/registro.php")
    Call<String> Postregistro(@Part("usuario") RequestBody usuario,
                              @Part("clave") RequestBody clave,
                              @Part("ID") RequestBody ID,
                              @Part("nombre1") RequestBody nombre1,
                              @Part("nombre2") RequestBody nombre2,
                              @Part("apaterno") RequestBody apaterno,
                              @Part("amaterno") RequestBody amaterno,
                              @Part("correo") RequestBody correo);



    @Multipart
    @POST("acceso/validar_usuario.php")
    Call<String> POstvalidar_usuario(@Part("usuario") RequestBody usuario,
                                     @Part("ID") RequestBody ID
    );



    @POST("test121")
    Call<String> sumita_chida(@Body RequestBody file);



    @Multipart
    @POST("RequestImageWithMetadata")
    Call<String> imagen_chida(  @Part("avatar_img\"; filename=\"foto.jpg\" ") RequestBody file,
                                @Part("some_text") RequestBody fname);

    @Multipart
    @POST("ch2")
    Call<String> ch2(@Part("n1") RequestBody nid ,
                                @Part("some_text") RequestBody nombre);








}

