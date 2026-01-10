/**
 * Module de validation des graphes de Markov (Étape 2)
 * 
 * Vérifie qu'un graphe respecte la propriété de Markov :
 * la somme des probabilités sortantes de chaque sommet doit être dans [0.99, 1]
 * 
 * Fournit deux fonctions de validation (une par RPI) et deux fonctions de test
 */
object Validation {
  
  /**
   * Vérifie si une matrice d'adjacence est un graphe de Markov valide
   * 
   * @param matrice la matrice d'adjacence à valider
   * @return liste vide si le graphe est valide,
   *         sinon liste des sommets dont la somme des probabilités n'est pas dans [0.99, 1]
   */
  def sommetsInvalides(matrice: MatriceAdjacence): List[Int] = {
    (1 to matrice.nbSommets).filter { sommet =>
      val somme = matrice.sommeSortante(sommet)
      // Un sommet est invalide si sa somme est > 0 et hors de [0.99, 1]
      somme > 0 && (somme < 0.99 || somme > 1.0)
    }.toList
  }
  
  /**
   * Vérifie si une liste d'adjacence est un graphe de Markov valide
   * 
   * @param liste la liste d'adjacence à valider
   * @return liste vide si le graphe est valide,
   *         sinon liste des sommets dont la somme des probabilités n'est pas dans [0.99, 1]
   */
  def sommetsInvalides(liste: ListeAdjacence): List[Int] = {
    (1 to liste.nbSommets).filter { sommet =>
      val somme = liste.sommeSortante(sommet)
      // Un sommet est invalide si sa somme est > 0 et hors de [0.99, 1]
      somme > 0 && (somme < 0.99 || somme > 1.0)
    }.toList
  }
  
  /**
   * Fonction de test pour MatriceAdjacence paramétrée par le nom du fichier
   * 
   * Charge un graphe depuis un fichier, vérifie sa validité et affiche le résultat
   * 
   * @param fichier chemin du fichier à tester
   */
  def testerMatrice(fichier: String): Unit = {
    println("=" * 70)
    println(s"TEST MATRICE D'ADJACENCE : $fichier")
    println("=" * 70)
    
    Chargeur.chargerMatrice(fichier) match {
      case Some(matrice) =>
        println(s"✓ Graphe chargé : ${matrice.nbSommets} sommets")
        println()
        
        val invalides = sommetsInvalides(matrice)
        
        if (invalides.isEmpty) {
          println("✓ RÉSULTAT : GRAPHE DE MARKOV VALIDE")
          println("  Tous les sommets ont une somme de probabilités dans [0.99, 1]")
        } else {
          println("✗ RÉSULTAT : GRAPHE DE MARKOV INVALIDE")
          println(s"  Sommets en cause : ${invalides.mkString(", ")}")
          println()
          println("  Détail des sommets invalides :")
          invalides.foreach { sommet =>
            val somme = matrice.sommeSortante(sommet)
            println(f"    Sommet $sommet%2d : somme = $somme%.4f (hors de [0.99, 1])")
          }
        }
        
      case None =>
        println("✗ Erreur : impossible de charger le fichier")
    }
    
    println("=" * 70)
    println()
  }
  
  /**
   * Fonction de test pour ListeAdjacence paramétrée par le nom du fichier
   * 
   * Charge un graphe depuis un fichier, vérifie sa validité et affiche le résultat
   * 
   * @param fichier chemin du fichier à tester
   */
  def testerListe(fichier: String): Unit = {
    println("=" * 70)
    println(s"TEST LISTE D'ADJACENCE : $fichier")
    println("=" * 70)
    
    Chargeur.chargerListe(fichier) match {
      case Some(liste) =>
        println(s"✓ Graphe chargé : ${liste.nbSommets} sommets")
        println()
        
        val invalides = sommetsInvalides(liste)
        
        if (invalides.isEmpty) {
          println("✓ RÉSULTAT : GRAPHE DE MARKOV VALIDE")
          println("  Tous les sommets ont une somme de probabilités dans [0.99, 1]")
        } else {
          println("✗ RÉSULTAT : GRAPHE DE MARKOV INVALIDE")
          println(s"  Sommets en cause : ${invalides.mkString(", ")}")
          println()
          println("  Détail des sommets invalides :")
          invalides.foreach { sommet =>
            val somme = liste.sommeSortante(sommet)
            println(f"    Sommet $sommet%2d : somme = $somme%.4f (hors de [0.99, 1])")
          }
        }
        
      case None =>
        println("✗ Erreur : impossible de charger le fichier")
    }
    
    println("=" * 70)
    println()
  }
  
  /**
   * Teste tous les fichiers d'exemples avec les deux représentations
   */
  def testerTousFichiers(): Unit = {
    val fichiers = List(
      "exemples/exemple1.txt",
      "exemples/exemple2.txt",
      "exemples/exemple3.txt",
      "exemples/exemple_meteo.txt",
      "exemples/exemple_valid_step3.txt"
    )
    
    println("\n")
    println("=" * 70)
    println("TESTS DE VALIDATION - ÉTAPE 2")
    println("=" * 70)
    println()
    
    fichiers.foreach { fichier =>
      // Test avec Matrice d'adjacence
      testerMatrice(fichier)
      
      // Test avec Liste d'adjacence
      testerListe(fichier)
    }
  }
}
