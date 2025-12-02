package com.example.tareasspring;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ListView lTareas;
    private TextView tTitulo, tDescripcion;
    private Spinner sPrioridad;
    private ImageButton bCrear;

    //Definición de retrofit
    private Retrofit retrofit;
    private ApiTareas api;

    //Definir adaptador estático para spinner
    ArrayAdapter<String> adaptadorPrioridad;
    String[] prioridades = {"baja","media","alta"};

    //Definir adaptador de listView
    private ArrayAdapter<Tarea> adaptadorTareas;
    //Lista que se carga en el adaptador
    private List<Tarea> tareas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Retrofit
        retrofit= new Retrofit.Builder()
                .baseUrl("http://54.159.142.234:8080/api/tareas/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiTareas.class);

        //Interfaz
        lTareas = (ListView) findViewById(R.id.lTareas);
        tTitulo = findViewById(R.id.tTitulo);
        tDescripcion = findViewById(R.id.tDescripcion);
        sPrioridad = findViewById(R.id.sPrioridad);
        bCrear =  findViewById(R.id.bCrear);

        //Cargar datos de Spinner
        adaptadorPrioridad = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,prioridades);
        sPrioridad.setAdapter(adaptadorPrioridad);

        //Cargar datos de listView
        adaptadorTareas = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, tareas);
        lTareas.setAdapter(adaptadorTareas);
        cargarTareas();
        
        //Acción de botón crear tarea
        bCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Crear tarea
                Tarea t = new Tarea();
                t.setTitulo(tTitulo.getText().toString());
                t.setDescripcion(tDescripcion.getText().toString());
                t.setPrioridad(sPrioridad.getSelectedItem().toString());
                t.setFecha_creacion(new SimpleDateFormat("yyyy-M-d").format(new Date()));
                //Configuración al endpoint crear tarea
                Call<Tarea> crearT = api.crearTarea(t);
                //Llamada asíncrona a la api
                crearT.enqueue(new Callback<Tarea>() {
                    @Override
                    public void onResponse(Call<Tarea> call, Response<Tarea> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            Toast.makeText(MainActivity.this, "Tarea cread con id:"+response.body().getId(),
                                    Toast.LENGTH_SHORT).show();
                            cargarTareas();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error al crear la tarea",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Tarea> call, Throwable throwable) {
                        Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //borrar con pulsación larga en listview
        lTareas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Tarea t = (Tarea) lTareas.getItemAtPosition(position);
                //Llamada a la api borrar
                //Configuración al endpoint crear tarea
                Call<Void> crearT = api.borrarTarea(t.getId());
                //Llamada asíncrona a la api
                crearT.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Tarea borrada",
                                    Toast.LENGTH_SHORT).show();
                            cargarTareas();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable throwable) {
                        Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
        });
    }

    private void cargarTareas() {
        //Configuración al endpoint listar
        Call<List<Tarea>> listarT = api.obtenerTareas();
        //Llamada asíncrona al endpoint
        listarT.enqueue(new Callback<List<Tarea>>() {
            @Override
            public void onResponse(Call<List<Tarea>> call, Response<List<Tarea>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    //Limipiar titulos
                    tareas.clear();
                    for (Tarea t:response.body()){
                        tareas.add(t);
                    }
                    adaptadorTareas.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(MainActivity.this, "Error al cargar tareas",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Tarea>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}