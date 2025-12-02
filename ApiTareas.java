package com.example.tareasspring;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiTareas {
    //Se añade a la URL base. NO PONER /listar
    @GET("listar")
    Call<List<Tarea>> obtenerTareas();

    @POST("guardar")
    Call<Tarea> crearTarea(@Body Tarea t);

    @DELETE("borrar")
    Call<Void> borrarTarea(@Query("id") Long id);
}
