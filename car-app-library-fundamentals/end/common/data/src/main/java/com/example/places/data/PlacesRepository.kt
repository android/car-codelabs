/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.places.data

import com.example.places.data.model.Place

val PLACES = listOf(
    Place(
        0,
        "Brooklyn Bridge",
        "The Brooklyn Bridge is a hybrid cable-stayed/suspension bridge in New York City, spanning the East River between the boroughs of Manhattan and Brooklyn. Opened on May 24, 1883, the Brooklyn Bridge was the first fixed crossing of the East River. It was also the longest suspension bridge in the world at the time of its opening, with a main span of 1,595.5 feet (486.3 m) and a deck 127 ft (38.7 m) above mean high water. The span was originally called the New York and Brooklyn Bridge or the East River Bridge but was officially renamed the Brooklyn Bridge in 1915.",
        40.7057,
        -73.9964
    ),
    Place(
        1,
        "Empire State Building",
        "The Empire State Building is a 102-story Art Deco skyscraper in Midtown Manhattan, New York City. The building was designed by Shreve, Lamb & Harmon and built from 1930 to 1931. Its name is derived from \"Empire State\", the nickname of the state of New York. The building has a roof height of 1,250 feet (380 m) and stands a total of 1,454 feet (443.2 m) tall, including its antenna. The Empire State Building stood as the world's tallest building until the construction of the World Trade Center in 1970; following the latter's collapse in 2001, the Empire State Building reverted to being the city's tallest skyscraper until that title was surpassed in 2012. As of 2022, the building is the seventh-tallest building in New York City, the ninth-tallest completed skyscraper in the United States, the 54th-tallest in the world, and the sixth-tallest freestanding structure in the Americas.",
        40.748333,
        -73.985278
    ),
    Place(
        2,
        "Statue of Liberty",
        "The Statue of Liberty (Liberty Enlightening the World; French: La Liberté éclairant le monde) is a colossal neoclassical sculpture on Liberty Island in New York Harbor in New York City, in the United States. The copper statue, a gift from the people of France, was designed by French sculptor Frédéric Auguste Bartholdi and its metal framework was built by Gustave Eiffel. The statue was dedicated on October 28, 1886.",
        40.689167,
        -74.044444
    ),
)

class PlacesRepository {
    fun getPlaces(): List<Place> {
        return PLACES
    }

    fun getPlace(placeId: Int): Place? {
        return PLACES.find { it.id == placeId }
    }
}