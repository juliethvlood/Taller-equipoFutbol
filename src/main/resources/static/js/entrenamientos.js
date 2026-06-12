const API =
"http://localhost:8080/entrenamientos";

document
.getElementById("formEntrenamiento")
.addEventListener(
"submit",
async (e)=>{

    e.preventDefault();

    const entrenamiento = {

        idJugador:
        document.getElementById("idJugador").value,

        duracion:
        document.getElementById("duracion").value
    };

    const respuesta =
    await fetch(API,{

        method:"POST",

        headers:{
            "Content-Type":
            "application/json"
        },

        body:
        JSON.stringify(entrenamiento)

    });

    const mensaje =
    await respuesta.text();

    document
    .getElementById("mensaje")
    .innerHTML = mensaje;
});