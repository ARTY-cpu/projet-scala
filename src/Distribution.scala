/**
 * Module pour gérer les distributions de probabilités
 * Partie 2 : Calculs de distributions sur les graphes de Markov
 * 
 * Une distribution Π est un vecteur ligne où chaque coordonnée
 * représente la probabilité d'être dans un état donné.
 * 
 * Propriété : Σ Π[i] = 1 (somme des probabilités = 1)
 * 
 * Utilise l'immutabilité et la programmation fonctionnelle
 */
class Distribution(val probas: Vector[Double]) {
  
  /**
   * Nombre d'états dans la distribution
   */
  def taille: Int = probas.length
  
  /**
   * Récupère la probabilité de l'état i (base 1)
   * 
   * @param i numéro de l'état (base 1)
   * @return probabilité de l'état i
   */
  def apply(i: Int): Double = {
    require(i >= 1 && i <= taille, s"Indice invalide: $i (taille: $taille)")
    probas(i - 1)
  }
  
  /**
   * Vérifie si la distribution est valide
   * La somme des probabilités doit être égale à 1
   * 
   * @param epsilon tolérance pour les erreurs d'arrondi
   * @return true si valide
   */
  def estValide(epsilon: Double = 0.0001): Boolean = {
    val somme = probas.sum
    math.abs(somme - 1.0) < epsilon
  }
  
  /**
   * Calcule l'évolution de la distribution : Π' = Π × M
   * 
   * Pour chaque état j : Π'[j] = Σ(i) Π[i] × M[i,j]
   * 
   * Utilise la programmation fonctionnelle avec map et sum
   * 
   * @param matrice matrice de transition
   * @return nouvelle distribution après une étape
   */
  def evoluer(matrice: MatriceAdjacence): Distribution = {
    require(taille == matrice.nbSommets, 
      s"Taille incompatible: distribution=${taille}, matrice=${matrice.nbSommets}")
    
    // Pour chaque état j, calcule Σ(i) Π[i] × M[i,j]
    val nouvellesProba = (1 to taille).map { j =>
      (1 to taille).map(i => probas(i - 1) * matrice.proba(i, j)).sum
    }.toVector
    
    new Distribution(nouvellesProba)
  }
  
  /**
   * Calcule l'évolution après n étapes : Π(n) = Π(0) × M^n
   * 
   * Optimisation : on peut aussi calculer M^n puis multiplier
   * 
   * @param matrice matrice de transition
   * @param n nombre d'étapes
   * @return distribution après n étapes
   */
  def evoluerN(matrice: MatriceAdjacence, n: Int): Distribution = {
    require(n >= 0, s"Nombre d'étapes doit être >= 0, reçu: $n")
    
    if (n == 0) {
      this
    } else {
      // Calcule M^n puis multiplie avec Π
      val puissance = CalculsMatriciels.puissance(matrice, n)
      evoluerAvecPuissance(puissance)
    }
  }
  
  /**
   * Multiplie la distribution par une matrice (déjà calculée)
   * 
   * @param matrice matrice de transition (peut être M^n)
   * @return nouvelle distribution
   */
  private def evoluerAvecPuissance(matrice: MatriceAdjacence): Distribution = {
    val nouvellesProba = (1 to taille).map { j =>
      (1 to taille).map(i => probas(i - 1) * matrice.proba(i, j)).sum
    }.toVector
    
    new Distribution(nouvellesProba)
  }
  
  /**
   * Affiche la distribution avec les noms des états
   * 
   * @param nomsEtats noms optionnels des états (si None, affiche juste les numéros)
   */
  def afficher(nomsEtats: Option[Vector[String]] = None): Unit = {
    println("\nDistribution Π:")
    println("-" * 60)
    
    nomsEtats match {
      case Some(noms) if noms.length == taille =>
        // Affiche avec les noms
        for (i <- 0 until taille) {
          println(f"  État ${i + 1}: ${noms(i)}%-15s → ${probas(i)}%.4f (${probas(i) * 100}%.2f%%)")
        }
      case _ =>
        // Affiche sans les noms
        for (i <- 0 until taille) {
          println(f"  État ${i + 1}: ${probas(i)}%.4f (${probas(i) * 100}%.2f%%)")
        }
    }
    
    println(f"\nSomme = ${probas.sum}%.6f")
    println("-" * 60)
  }
  
  /**
   * Affiche la distribution en format compact (vecteur ligne)
   */
  def afficherCompact(): Unit = {
    val str = probas.map(p => f"$p%.2f").mkString("(", " ", ")")
    println(s"Π = $str")
  }
  
  /**
   * Calcule la différence avec une autre distribution
   * 
   * @param autre autre distribution
   * @return somme des différences absolues
   */
  def difference(autre: Distribution): Double = {
    require(taille == autre.taille, 
      s"Tailles incompatibles: $taille vs ${autre.taille}")
    
    probas.zip(autre.probas)
      .map { case (p1, p2) => math.abs(p1 - p2) }
      .sum
  }
}

/**
 * Objet companion pour créer des distributions
 */
object Distribution {
  
  /**
   * Crée une distribution à partir d'un état initial unique
   * Π[i] = 1, tous les autres = 0
   * 
   * @param n nombre d'états
   * @param etatInitial état de départ (base 1)
   * @return distribution initiale
   */
  def depuisEtat(n: Int, etatInitial: Int): Distribution = {
    require(etatInitial >= 1 && etatInitial <= n, 
      s"État initial invalide: $etatInitial (taille: $n)")
    
    val probas = Vector.tabulate(n)(i => if (i == etatInitial - 1) 1.0 else 0.0)
    new Distribution(probas)
  }
  
  /**
   * Crée une distribution uniforme
   * Tous les états ont la même probabilité 1/n
   * 
   * @param n nombre d'états
   * @return distribution uniforme
   */
  def uniforme(n: Int): Distribution = {
    require(n > 0, s"Taille doit être > 0, reçu: $n")
    
    val proba = 1.0 / n
    val probas = Vector.fill(n)(proba)
    new Distribution(probas)
  }
  
  /**
   * Crée une distribution à partir d'un vecteur de probabilités
   * 
   * @param probas vecteur de probabilités
   * @return distribution
   */
  def depuisVecteur(probas: Vector[Double]): Distribution = {
    require(probas.nonEmpty, "Vecteur de probabilités vide")
    require(probas.forall(p => p >= 0 && p <= 1), 
      "Toutes les probabilités doivent être entre 0 et 1")
    
    new Distribution(probas)
  }
  
  /**
   * Extrait une ligne de matrice comme distribution
   * Utile pour analyser les distributions stationnaires
   * 
   * @param matrice matrice source
   * @param ligne numéro de ligne (base 1)
   * @return distribution correspondant à cette ligne
   */
  def depuisLigneMatrice(matrice: MatriceAdjacence, ligne: Int): Distribution = {
    require(ligne >= 1 && ligne <= matrice.nbSommets, 
      s"Ligne invalide: $ligne")
    
    val probas = (1 to matrice.nbSommets)
      .map(j => matrice.proba(ligne, j))
      .toVector
    
    new Distribution(probas)
  }
}
