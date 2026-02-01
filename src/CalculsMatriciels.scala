/**
 * Module pour les opérations matricielles avancées
 * Partie 2 : Calculs sur les graphes de Markov
 * 
 * Opérations implémentées :
 * - Conversion ListeAdjacence → MatriceAdjacence
 * - Multiplication de matrices
 * - Calcul de la différence entre deux matrices
 * - Puissance de matrice (M^n)
 * 
 * Utilise la programmation fonctionnelle et l'immutabilité
 */
object CalculsMatriciels {
  
  /**
   * Convertit une liste d'adjacence en matrice d'adjacence
   * 
   * Crée une matrice n×n où chaque cellule (i,j) contient
   * la probabilité de transition du sommet i vers le sommet j
   * 
   * @param liste liste d'adjacence source
   * @return matrice d'adjacence équivalente
   */
  def listeVersMatrice(liste: ListeAdjacence): MatriceAdjacence = {
    val n = liste.nbSommets
    val matrice = new MatriceAdjacence(n)
    
    // Parcourt tous les sommets et remplit la matrice
    for (i <- 1 to n) {
      val successeurs = liste.successeurs(i)
      successeurs.foreach { case (j, proba) =>
        matrice.setProba(i, j, proba)
      }
    }
    
    matrice
  }
  
  /**
   * Multiplie deux matrices n×n
   * 
   * Calcul standard : C[i,j] = Σ(k=1 to n) A[i,k] × B[k,j]
   * 
   * Utilise la programmation fonctionnelle avec map et sum
   * 
   * @param a première matrice
   * @param b seconde matrice
   * @return matrice produit A × B
   */
  def multiplier(a: MatriceAdjacence, b: MatriceAdjacence): MatriceAdjacence = {
    require(a.nbSommets == b.nbSommets, 
      s"Tailles incompatibles: ${a.nbSommets} vs ${b.nbSommets}")
    
    val n = a.nbSommets
    val resultat = new MatriceAdjacence(n)
    
    // Pour chaque cellule (i,j) du résultat
    for (i <- 1 to n; j <- 1 to n) {
      // Produit scalaire de la ligne i de A avec la colonne j de B
      val valeur = (1 to n).map(k => a.proba(i, k) * b.proba(k, j)).sum
      // Arrondir pour éviter les dépassements dus aux erreurs d'arrondi
      val valeurArrondie = math.min(1.0, math.max(0.0, valeur))
      resultat.setProba(i, j, valeurArrondie)
    }
    
    resultat
  }
  
  /**
   * Calcule la puissance d'une matrice : M^n
   * 
   * Méthode : multiplications successives
   * M^0 = I (matrice identité)
   * M^1 = M
   * M^n = M × M^(n-1)
   * 
   * @param matrice matrice de base
   * @param n exposant (doit être >= 0)
   * @return matrice M^n
   */
  def puissance(matrice: MatriceAdjacence, n: Int): MatriceAdjacence = {
    require(n >= 0, s"Exposant doit être >= 0, reçu: $n")
    
    if (n == 0) {
      // Matrice identité
      creerIdentite(matrice.nbSommets)
    } else if (n == 1) {
      // Copie de la matrice
      copier(matrice)
    } else {
      // Multiplication successive
      var resultat = copier(matrice)
      for (_ <- 2 to n) {
        resultat = multiplier(resultat, matrice)
      }
      resultat
    }
  }
  
  /**
   * Calcule la différence entre deux matrices
   * 
   * diff(M, N) = Σ(i,j) |M[i,j] - N[i,j]|
   * 
   * Somme des valeurs absolues des différences entre
   * tous les coefficients des deux matrices
   * 
   * Utilise HOF map et sum
   * 
   * @param m première matrice
   * @param n seconde matrice
   * @return somme des différences absolues
   */
  def difference(m: MatriceAdjacence, n: MatriceAdjacence): Double = {
    require(m.nbSommets == n.nbSommets, 
      s"Tailles incompatibles: ${m.nbSommets} vs ${n.nbSommets}")
    
    val taille = m.nbSommets
    
    // Calcule la somme des différences absolues
    (for {
      i <- 1 to taille
      j <- 1 to taille
    } yield math.abs(m.proba(i, j) - n.proba(i, j))).sum
  }
  
  /**
   * Crée une matrice identité de taille n
   * I[i,j] = 1 si i == j, 0 sinon
   * 
   * @param n taille de la matrice
   * @return matrice identité n×n
   */
  private def creerIdentite(n: Int): MatriceAdjacence = {
    val identite = new MatriceAdjacence(n)
    for (i <- 1 to n) {
      identite.setProba(i, i, 1.0)
    }
    identite
  }
  
  /**
   * Copie une matrice
   * 
   * @param source matrice à copier
   * @return nouvelle matrice identique
   */
  private def copier(source: MatriceAdjacence): MatriceAdjacence = {
    val n = source.nbSommets
    val copie = new MatriceAdjacence(n)
    
    for (i <- 1 to n; j <- 1 to n) {
      copie.setProba(i, j, source.proba(i, j))
    }
    
    copie
  }
  
  /**
   * Trouve la puissance n pour laquelle M^n converge (distribution stationnaire)
   * 
   * Continue de calculer M^n jusqu'à ce que la différence entre
   * M^n et M^(n-1) soit inférieure à epsilon
   * 
   * @param matrice matrice de base
   * @param epsilon seuil de convergence (par défaut 0.01)
   * @param maxIterations nombre max d'itérations (sécurité)
   * @return (n, M^n, converge?) où n est la puissance trouvée
   */
  def trouverConvergence(
    matrice: MatriceAdjacence, 
    epsilon: Double = 0.01,
    maxIterations: Int = 1000
  ): (Int, MatriceAdjacence, Boolean) = {
    
    var precedente = copier(matrice)  // M^1
    var courante = multiplier(matrice, matrice)  // M^2
    var n = 2
    var converge = false
    
    while (n < maxIterations && !converge) {
      val diff = difference(courante, precedente)
      
      if (diff < epsilon) {
        converge = true
      } else {
        precedente = courante
        courante = multiplier(courante, matrice)
        n += 1
      }
    }
    
    (n, courante, converge)
  }
  
  /**
   * Affiche une matrice avec un titre
   * 
   * @param titre titre à afficher
   * @param matrice matrice à afficher
   */
  def afficherAvecTitre(titre: String, matrice: MatriceAdjacence): Unit = {
    println("\n" + "=" * 60)
    println(titre)
    println("=" * 60)
    matrice.afficher()
  }
}
