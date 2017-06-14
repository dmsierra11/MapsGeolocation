package com.example.locationservices.model.dummy;

import com.example.locationservices.model.Coords;
import com.example.locationservices.model.Place;

import java.util.ArrayList;
import java.util.List;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * Created by danielsierraf on 1/9/17.
 */

public class DummyContent {
    private static final String TAG = "DummyContent";
    public static List<Place> your_places = new ArrayList<>();

    public static List<Place> createPlaces(){
        //Madrid
        Place place = new Place("Arepa Olé");

        place.setCoords(new Coords(40.421767, -3.698556));
        place.setDesc("Ven con tus amigos y elige entre nuestras arepas de reina pepeada, catira y " +
                "queso amarillo. Tómate un selfie comiéndotela y compártelo en instagram o twitter " +
                "mencionándonos @arepaole. Participas por una cena para 2 personas #rutadelaarepa");

        Place place2 = new Place("Grama Bar");
        place2.setCoords(new Coords(40.420016, -3.707073));
        place2.setDesc("Consume por lo menos dos de nuestra arepa especial \'Mi negra\' con alguna " +
                "bebida. Compártela con tres amigos por instagram haciendo mención a nuestra cuenta " +
                "@gramabar y participaras por un delicioso premio #rutadelaarepa");

        Place place3 = new Place("Abrásame Malasaña");
        place3.setCoords(new Coords(40.424031, -3.706351));
        place3.setDesc("En Abrásame estamos muy orgullosos de presentar nuestra arepa \"La Bárbara\" " +
                "ven a probarla y comparte tu selfie con el hashtag #rutadelaarepa y mención a " +
                "@abrasamemalasana. Participarás por deliciosos premios.");

        Place place4 = new Place("Antojos Araguaney");
        place4.setCoords(new Coords(40.504371, -3.671897));
        place4.setDesc("En la 1era Ruta de la Arepa en Madrid estarán participando con una Arepa de " +
                "chistorra. Tómate tu selfie y participa por premios muy sabrosos. No olvides el " +
                "hastag #rutadelaarepa y mencionarnos @antojosaraguaney");

        Place place5 = new Place("El Bombón Madrid");
        place5.setCoords(new Coords(40.390623, -3.626916));
        place5.setDesc("Vente esta noche a probar esta deliciosa arepa de pulpo ahumado crocante con" +
                " la que estamos participando en la 1ra ruta de la arepa Pan en Madrid #LaLatina" +
                "#rutadelaarepa @elbombonmadrid");

        Place place6 = new Place("Restaurante La Candelita");
        place6.setCoords(new Coords(40.422756, -3.695541));
        place6.setDesc("Te invitamos a probar nuestra arepa de solomillo ibérico a la parrilla adobado " +
                "con pimienta guayabita, Merkén  y aceite de oliva acompañada de aguacate y pebre " +
                "chileno. Comparte tu selfie mencionando nuestra cuenta @misscandelita para " +
                "participar en nuestro sorteo a realizarse el 11 de junio por una bandeja de tequeños.");

        Place place7 = new Place("La Arepera By La Cuchara");
        place7.setCoords(new Coords(40.424192, -3.700936));
        place7.setDesc("Ufff una Arepa Domino !! No importa como las conozcas Alubias Negras, " +
                "Frijoles o Caraotas … están bueniiísimas!! Y el Queso Blanco uff que sabor! No la " +
                "dejes de disfrutar en @laarepera.es");

        your_places.add(place);
        your_places.add(place2);
        your_places.add(place3);
        your_places.add(place4);
        your_places.add(place5);
        your_places.add(place6);
        your_places.add(place7);
        return your_places;
    }

    public static final class SessionIdentifierGenerator {
        private static SecureRandom random = new SecureRandom();

        public static String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
}
