document.addEventListener("DOMContentLoaded", hentData);

function hentData()
{
  fetch("data.json")
    .then(function (svar)
    {
      if (!svar.ok)
      {
        throw new Error("data.json kunne ikke hentes (" + svar.status + ")");
      }
      return svar.json();
    })
    .then(function (data)
    {
      visTal(data);
      visGroenneTiltag(data.groenneTiltag);
    })
    .catch(function (fejl)
    {
      console.error(fejl);
      visFejl();
    });
}

function saetTal(id, vaerdi)
{
  var element = document.getElementById(id);
  if (element)
  {
    element.textContent = vaerdi;
  }
}

function visTal(data)
{
  saetTal("antal-beboere", data.antalBeboere);
  saetTal("udfoerte-opgaver", data.udfoerteOpgaver);
  saetTal("personlige-point", data.personligePoint);
  saetTal("faelles-point", data.faellesPoint);
  saetTal("antal-groenne-tiltag", data.groenneTiltag.length);
}

function visGroenneTiltag(tiltag)
{
  var liste = document.getElementById("tiltag-liste");
  if (!liste)
  {
    return;
  }

  liste.innerHTML = "";

  if (tiltag.length === 0)
  {
    liste.appendChild(lavBesked(
      "Ingen grønne tiltag endnu",
      "Når Kløverly gennemfører et grønt tiltag, vises det her."
    ));
    return;
  }

  tiltag.forEach(function (t)
  {
    var kort = document.createElement("article");
    kort.className = "tiltag-card";

    var maerke = document.createElement("span");
    maerke.className = "tiltag-badge";
    maerke.textContent = "GRØNT TILTAG";

    var titel = document.createElement("h3");
    titel.textContent = t.beskrivelse; // textContent: tekst vises som tekst, ikke som HTML

    var info = document.createElement("div");
    info.className = "tiltag-info";
    info.appendChild(lavLinje("Dato", formaterDato(t.dato), ""));
    info.appendChild(lavLinje("Bidrag til fællesskabet", "+" + t.point + " point", "point-value"));

    kort.appendChild(maerke);
    kort.appendChild(titel);
    kort.appendChild(info);
    liste.appendChild(kort);
  });
}

function lavLinje(overskrift, tekst, klasse)
{
  var linje = document.createElement("p");
  var fed = document.createElement("strong");
  fed.textContent = overskrift;
  var vaerdi = document.createElement("span");
  vaerdi.textContent = tekst;
  if (klasse)
  {
    vaerdi.className = klasse;
  }
  linje.appendChild(fed);
  linje.appendChild(vaerdi);
  return linje;
}

function lavBesked(overskrift, tekst)
{
  var boks = document.createElement("div");
  boks.className = "empty-box";
  var h3 = document.createElement("h3");
  h3.textContent = overskrift;
  var p = document.createElement("p");
  p.textContent = tekst;
  boks.appendChild(h3);
  boks.appendChild(p);
  return boks;
}

/* "2026-10-13" bliver til "13. oktober 2026". */
function formaterDato(isoDato)
{
  var dele = isoDato.split("-");
  var dato = new Date(dele[0], dele[1] - 1, dele[2]);
  return dato.toLocaleDateString("da-DK", { day: "numeric", month: "long", year: "numeric" });
}

/* Vises, hvis data.json ikke kan hentes, fx når siden er åbnet direkte som fil. */
function visFejl()
{
  var liste = document.getElementById("tiltag-liste");
  var besked = lavBesked(
    "Data kunne ikke hentes",
    "Åbn siden via IntelliJ (knappen med browser-ikonet øverst til højre i editoren), så den vises gennem en lokal webserver."
  );
  if (liste)
  {
    liste.innerHTML = "";
    liste.appendChild(besked);
  }
  else
  {
    var stats = document.querySelector(".stats .container");
    if (stats)
    {
      stats.appendChild(besked);
    }
  }
}
