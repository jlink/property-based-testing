# Property-based Testing in Java - The Importance of Being Shrunk

Shrinking
   Ein weiteres Problem, das bei randomisierter Generierung auftaucht, 
   ist der manchmal nur schwer zu erkennende Zusammenhang zwischen 
   den generierten Werten und dem Bug im Code. 
   Betrachten wir das folgende Beispiel:


```java
static <E> List<E> brokenReverse(List<E> aList) {
    if (aList.size() < 4) {
        aList = new ArrayList<>(aList);
            reverse(aList);
        }
        return aList;
    }
 
@Property(shrinking = ShrinkingMode.OFF)
boolean reverseShouldSwapFirstAndLast(@ForAll List<Integer> aList) {
    Assume.that(!aList.isEmpty());
    List<Integer> reversed = brokenReverse(aList);
    return aList.get(0) == reversed.get(aList.size() - 1);
}
```   
   
   In diesem Beispiel testen wir eine kaputte Implementierung brokenReverse. 
   Ein erster Testlauf liefert ein Ergebnis, das in etwa so aussieht:
     org.opentest4j.AssertionFailedError: 
     Property [reverseShouldSwapFirstAndLast] falsified with sample 
         [[0, 1, -1, -2147483648, 2147483647, -247, 247, 39, -31, 39, 477784874]]

Welche Eigenschaft dieser Liste ist f�r das Fehlverhalten verantwortlich? 
Sind es die negativen Zahlen? Ist es der doppelte Eintrag? Da hilft nur Debuggen, oder?

    Geben wir jqwik noch eine Chance und schalten diesmal das �Schrumpfen� ein, 
    indem wir ShrinkingMode.ON  verwenden � oder diesen Eintrag entfernen. 
    Dann sieht das Ergebnis ein wenig anders aus:
     org.opentest4j.AssertionFailedError: 
     Property [reverseShouldSwapFirstAndLast] falsified with sample 
         [[0, 0, 0, -1]]
         
   Das ist doch ein deutlich angenehmeres Beispiel, das uns fast den Implementierungsfehler 
   erraten l�sst: Es k�nnte mit der L�nge der Liste zu tun haben � und genau so ist es ja auch.
   
   Hinter den Kulissen hat jqwik die urspr�nglich fehlschlagende Beispiel-Liste genommen, 
   und diese �geschrumpft�, um ein m�glichst einfaches und aussagekr�ftiges Sample zu erhalten. 
   Mit dieser als Shrinking bezeichneten Funktionalit�t, sind die meisten Property-Testbibliotheken 
   ausgestattet. 
   Typischerweise findet dieses Schrumpfen ausschlie�lich auf Basis des statischen Typs statt, 
   was jedoch zu dem Problem f�hren kann, dass ein geschrumpfter Wert sich au�erhalb des 
   spezifizierten Bereichs bewegen kann. 
   Aus diesem Grund setzt jqwik auf Integrated Shrinking [6], was aufw�ndiger zu implementieren ist, 
   daf�r aber garantiert, dass geschrumpfte Werte die gleichen Eigenschaften besitzen 
   wie generierte Werte.

[6] Integrated vs type based shrinking. http://hypothesis.works/articles/integrated-shrinking/
