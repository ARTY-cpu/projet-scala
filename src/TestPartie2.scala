/**
 * Programme de test pour la Partie 2
 * Validation des calculs matriciels et distributions
 */
object TestPartie2 extends App {
  
  println("=" * 70)
  println("PARTIE 2 : CALCULS MATRICIELS ET DISTRIBUTIONS")
  println("=" * 70)
  
  // ===========================================================================
  // TEST 1 : Chargement et affichage de la matrice météo
  // ===========================================================================
  
  println("\n\n>>> TEST 1 : Chargement exemple_meteo.txt")
  println("-" * 70)
  
  val fichierMeteo = "exemples/exemple_meteo.txt"
  
  Chargeur.charger(fichierMeteo) match {
    case Some(graphe) =>
      println(s" Graphe chargé: ${graphe.nbSommets} sommets")
      
      // Convertir en matrice si c'est une liste
      val matrice = graphe match {
        case m: MatriceAdjacence => m
        case l: ListeAdjacence => 
          println("  Conversion ListeAdjacence -> MatriceAdjacence")
          CalculsMatriciels.listeVersMatrice(l)
      }
      
      println("\nMatrice M (transitions météorologiques):")
      matrice.afficher()
      
      // Noms des états météo
      val nomsEtats = Vector(
        "1-Sunny", 
        "2-Cloudy", 
        "3-Rain", 
        "4-Storm", 
        "5-Sunny_spells"
      )
      
      // ===========================================================================
      // TEST 2 : Calcul de M³
      // ===========================================================================
      
      println("\n\n>>> TEST 2 : Calcul de M³")
      println("-" * 70)
      
      val m3 = CalculsMatriciels.puissance(matrice, 3)
      CalculsMatriciels.afficherAvecTitre("M³ (prévisions à 3 jours)", m3)
      
      // Vérification avec les valeurs attendues
      println("\nVérification des valeurs attendues pour M³:")
      println("  M³[1,1] attendu: 0.17 -> obtenu: " + f"${m3.proba(1, 1)}%.2f")
      println("  M³[1,2] attendu: 0.37 -> obtenu: " + f"${m3.proba(1, 2)}%.2f")
      println("  M³[1,3] attendu: 0.13 -> obtenu: " + f"${m3.proba(1, 3)}%.2f")
      println("  M³[1,4] attendu: 0.05 -> obtenu: " + f"${m3.proba(1, 4)}%.2f")
      println("  M³[1,5] attendu: 0.27 -> obtenu: " + f"${m3.proba(1, 5)}%.2f")
      
      // ===========================================================================
      // TEST 3 : Calcul de M⁷
      // ===========================================================================
      
      println("\n\n>>> TEST 3 : Calcul de M⁷")
      println("-" * 70)
      
      val m7 = CalculsMatriciels.puissance(matrice, 7)
      CalculsMatriciels.afficherAvecTitre("M⁷ (prévisions à 7 jours)", m7)
      
      // Vérification avec les valeurs attendues
      println("\nVérification des valeurs attendues pour M⁷:")
      println("  M⁷[1,1] attendu: 0.16 -> obtenu: " + f"${m7.proba(1, 1)}%.2f")
      println("  M⁷[1,2] attendu: 0.36 -> obtenu: " + f"${m7.proba(1, 2)}%.2f")
      println("  M⁷[1,3] attendu: 0.13 -> obtenu: " + f"${m7.proba(1, 3)}%.2f")
      println("  M⁷[1,4] attendu: 0.05 -> obtenu: " + f"${m7.proba(1, 4)}%.2f")
      println("  M⁷[1,5] attendu: 0.29 -> obtenu: " + f"${m7.proba(1, 5)}%.2f")
      
      // Vérifier que toutes les lignes sont similaires (distribution stationnaire)
      println("\nVérification de la convergence (toutes les lignes similaires):")
      for (i <- 1 to 5) {
        print(f"  Ligne $i: ")
        for (j <- 1 to 5) {
          print(f"${m7.proba(i, j)}%.2f ")
        }
        println()
      }
      
      // ===========================================================================
      // TEST 4 : Distributions et évolutions
      // ===========================================================================
      
      println("\n\n>>> TEST 4 : Distributions et évolutions")
      println("-" * 70)
      
      // Distribution initiale : il fait beau (Sunny)
      val pi0 = Distribution.depuisEtat(5, 1)
      println("\nDistribution initiale Π(0) : Sunny (état 1)")
      pi0.afficher(Some(nomsEtats))
      
      // Évolution après 3 jours
      println("\n>>> Évolution après 3 jours : Π(3) = Π(0) × M³")
      val pi3 = pi0.evoluerN(matrice, 3)
      pi3.afficher(Some(nomsEtats))
      
      println("\nRéponse à la question 1:")
      println(f"  'Quelle probabilité que le temps soit nuageux dans 3 jours")
      println(f"   s'il fait beau aujourd'hui?'")
      println(f"  -> ${pi3(2) * 100}%.1f%% (état 2: Cloudy)")
      
      // Distribution initiale : il pleut (Rain)
      println("\n" + "-" * 70)
      val pi0_rain = Distribution.depuisEtat(5, 3)
      println("\nDistribution initiale Π(0) : Rain (état 3)")
      pi0_rain.afficher(Some(nomsEtats))
      
      // Évolution après 7 jours
      println("\n>>> Évolution après 7 jours : Π(7) = Π(0) × M⁷")
      val pi7 = pi0_rain.evoluerN(matrice, 7)
      pi7.afficher(Some(nomsEtats))
      
      println("\nRéponse à la question 2:")
      println(f"  'Quelles probabilités dans 7 jours s'il pleut aujourd'hui?'")
      println(f"  -> Distribution ci-dessus")
      
      // ===========================================================================
      // TEST 5 : Recherche de la distribution stationnaire
      // ===========================================================================
      
      println("\n\n>>> TEST 5 : Recherche de la distribution stationnaire")
      println("-" * 70)
      
      val (n, mn, converge) = CalculsMatriciels.trouverConvergence(matrice, epsilon = 0.01)
      
      if (converge) {
        println(f" Convergence atteinte après $n itérations")
        println(f"  (différence < 0.01 entre M^$n et M^${n-1})")
        
        // Affiche la distribution stationnaire (première ligne)
        val piStationnaire = Distribution.depuisLigneMatrice(mn, 1)
        println("\nDistribution stationnaire Π∞:")
        piStationnaire.afficher(Some(nomsEtats))
        
        println("\nRéponse à la question 3:")
        println("  'Atteint-on des probabilités indépendantes de la distribution")
        println("   de départ au bout d'un certain temps?'")
        println(f"  -> OUI, convergence en $n étapes vers la distribution stationnaire")
      } else {
        println(f" Pas de convergence après $n itérations")
      }
      
    case None =>
      println(s" Échec du chargement de $fichierMeteo")
  }
  
  // ===========================================================================
  // TEST 6 : Convergence sur tous les exemples
  // ===========================================================================
  
  println("\n\n>>> TEST 6 : Convergence sur tous les exemples")
  println("-" * 70)
  
  val exemples = List(
    "exemples/exemple1.txt",
    "exemples/exemple2.txt",
    "exemples/exemple3.txt",
    "exemples/exemple_meteo.txt",
    "exemples/exemple_valid_step3.txt"
  )
  
  println("\nRecherche de convergence (ε = 0.01) :")
  println()
  
  for (fichier <- exemples) {
    Chargeur.charger(fichier) match {
      case Some(graphe) =>
        val matrice = graphe match {
          case m: MatriceAdjacence => m
          case l: ListeAdjacence => CalculsMatriciels.listeVersMatrice(l)
        }
        
        val nomFichier = fichier.split("/").last
        val (n, _, converge) = CalculsMatriciels.trouverConvergence(matrice, epsilon = 0.01)
        
        if (converge) {
          println(f"   $nomFichier%-30s -> convergence en $n%3d itérations")
        } else {
          println(f"   $nomFichier%-30s -> PAS de convergence")
        }
        
      case None =>
        println(f"   ${fichier.split("/").last}%-30s -> Échec chargement")
    }
  }
  
  println("\n" + "=" * 70)
  println("FIN DES TESTS PARTIE 2")
  println("=" * 70)
}
