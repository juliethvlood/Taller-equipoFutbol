const API =
"http://localhost:8080/jugadores";

const formulario =
document.getElementById("formJugador");

const tabla =
document.getElementById("tablaJugadores");

listarJugadores();

formulario.addEventListener(
"submit",
async (e) => {

    e.preventDefault();

    const jugador = {

        nombre:
        document.getElementById("nombre").value,

        apellido:
        document.getElementById("apellido").value,

        edad:
        parseInt(
        document.getElementById("edad").value)

    };

    await fetch(API, {

        method: "POST",

        headers: {
            "Content-Type":
            "application/json"
        },

        body: JSON.stringify(jugador)

    });

    formulario.reset();

    listarJugadores();
});

async function listarJugadores() {

    const respuesta =
    await fetch(API);

    const jugadores =
    await respuesta.json();

    tabla.innerHTML = "";

    jugadores.forEach(jugador => {

        tabla.innerHTML += `
        <tr>
            <td>${jugador.idJugador}</td>
            <td>${jugador.nombre}</td>
            <td>${jugador.apellido}</td>
            <td>${jugador.edad}</td>
        </tr>
        `;
    });
}