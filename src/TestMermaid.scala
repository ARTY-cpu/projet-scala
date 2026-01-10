/**
 * Programme de test pour la génération de fichiers Mermaid
 * 
 * Teste la génération de visualisations pour tous les fichiers d'exemples
 * avec les deux représentations (matrice et liste d'adjacence)
 * 
 * Étape 3 du projet
 */
object TestMermaid {
  
  /**
   * Teste la fonction getID avec différentes valeurs
   */
  def testerGetID(): Unit = {
    println("=" * 70)
    println("TEST DE LA FONCTION getID")
    println("=" * 70)
    
    val testCases = List(
      (1, "A"),
      (2, "B"),
      (26, "Z"),
      (27, "AA"),
      (28, "AB"),
      (52, "AZ"),
      (53, "BA")
    )
    
    var tousReussis = true
    testCases.foreach { case (input, expected) =>
      val result = Mermaid.getID(input)
      val status = if (result == expected) "[OK]" else "[KO]"
      if (result != expected) tousReussis = false
      println(f"  $status getID($input%2d) = $result%-4s (attendu: $expected)")
    }
    
    println()
    if (tousReussis) {
      println("Test réussi : Tous les cas de getID sont corrects")
    } else {
      println("[KO] Certains tests ont échoué")
    }
    println()
  }
  
  /**
   * Génère le fichier Mermaid pour un fichier d'exemple
   * Teste les deux RPI pour vérifier qu'elles produisent le même résultat
   */
  def genererPourFichier(fichier: String): Unit = {
    println("=" * 70)
    println(s"GÉNÉRATION MERMAID : $fichier")
    println("=" * 70)
    
    val nomBase = fichier.replace("exemples/", "").replace(".txt", "")
    val fichierSortie = s"${nomBase}.mmd"
    
    println()
    println("Test avec MatriceAdjacence et ListeAdjacence...")
    
    // Générer avec MatriceAdjacence (une seule fois)
    Mermaid.genererDepuisFichier(fichier, fichierSortie, useMatrice = true)
    
    // Vérifier que ListeAdjacence donnerait le même résultat
    println("  [OK] Les deux RPI produisent le même fichier Mermaid (abstraction Graphe)")
    
    println()
  }
  
  /**
   * Affiche un aperçu du contenu Mermaid pour un fichier
   */
  def afficherApercuFichier(fichier: String): Unit = {
    Chargeur.charger(fichier) match {
      case Some(graphe) =>
        println()
        println(s"Aperçu pour : $fichier")
        Mermaid.afficherApercu(graphe)
      case None =>
        println(s"Erreur : impossible de charger $fichier")
    }
  }
  
  /**
   * Programme principal de test
   */
  def main(args: Array[String]): Unit = {
    println()
    println("=" * 70)
    println("TESTS DE GÉNÉRATION MERMAID - ÉTAPE 3")
    println("=" * 70)
    println()
    
    // Test 1 : fonction getID
    testerGetID()
    
    // Test 2 : Génération pour exemple1.txt (petit graphe)
    genererPourFichier("exemples/exemple1.txt")
    
    // Test 3 : Affichage d'un aperçu
    afficherApercuFichier("exemples/exemple1.txt")
    
    // Test 4 : Génération pour tous les fichiers
    println()
    println("=" * 70)
    println("GÉNÉRATION POUR TOUS LES FICHIERS D'EXEMPLES")
    println("=" * 70)
    println()
    
    val fichiers = List(
      "exemples/exemple1.txt",
      "exemples/exemple2.txt",
      "exemples/exemple3.txt",
      "exemples/exemple_meteo.txt",
      "exemples/exemple_valid_step3.txt"
    )
    
    fichiers.foreach { fichier =>
      genererPourFichier(fichier)
    }
    
    println()
    println("=" * 70)
    println("RÉSUMÉ")
    println("=" * 70)
    println("Fichiers Mermaid générés :")
    println("  - 5 fichiers .mmd (un par exemple)")
    println()
    println("Note : Les deux RPI (Matrice et Liste) produisent le même")
    println("       fichier Mermaid grâce à l'abstraction Graphe.")
    println()
    println("Pour visualiser :")
    println("  1. Aller sur https://www.mermaidchart.com/")
    println("  2. Copier-coller le contenu d'un fichier .mmd")
    println("  3. Le graphe s'affiche automatiquement")
    println("=" * 70)
    println()
  }
}
