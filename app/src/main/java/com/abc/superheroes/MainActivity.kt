package com.abc.superheroes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring.DampingRatioLowBouncy
import androidx.compose.animation.core.Spring.DampingRatioNoBouncy
import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.abc.superheroes.data.HeroListDataProvider
import com.abc.superheroes.data.HeroesRepository
import com.abc.superheroes.model.Hero
import com.abc.superheroes.ui.theme.SuperheroesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SuperheroesApp() }
    }
}


@PreviewScreenSizes
@Composable
fun SuperheroesApp() {
    SuperheroesTheme {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = { TopBar() }) { innerPadding ->
            SuperheroesAppLayout(
                heroes = HeroesRepository.heroes,
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displayLarge)
        }
    }, modifier = modifier)
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SuperheroesAppLayout(
    @PreviewParameter(HeroListDataProvider::class) heroes: List<Hero>, modifier: Modifier = Modifier
) {
    // To see previews without animation:
    val initialAnimationState = LocalInspectionMode.current // false

    val visibleState = remember {
        MutableTransitionState(initialAnimationState).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    // Fade in entry animation for the entire list
    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = fadeIn(animationSpec = spring(dampingRatio = DampingRatioLowBouncy)),
        exit = fadeOut()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            itemsIndexed(heroes) { index, hero ->
                HeroListItem(
                    // Animate each list item to slide in vertically
                    hero,
                    modifier = Modifier.animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = spring(
                                stiffness = StiffnessVeryLow,
                                dampingRatio = DampingRatioNoBouncy),
                            initialOffsetY = { it * (index + 1) } // staggered entrance
                        )
                    )
                )
            }
        }
    }
}


@Composable
fun HeroListItem(hero: Hero, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.card_elevation))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium))
                .sizeIn(minHeight = dimensionResource(R.dimen.card_min_content_height))
        ) {
            HeroInfo(
                name = hero.nameRes,
                description = hero.descriptionRes,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
            HeroPhoto(hero.imageRes)
        }
    }
}

@Composable
fun HeroInfo(@StringRes name: Int, @StringRes description: Int, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(stringResource(name), style = MaterialTheme.typography.displaySmall)
        Text(stringResource(description), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HeroPhoto(@DrawableRes heroIcon: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(heroIcon),
        contentDescription = null,
        modifier = modifier
            .size(dimensionResource(R.dimen.card_image_size))
            .clip(MaterialTheme.shapes.small),
        alignment = Alignment.Center,
        contentScale = ContentScale.FillWidth,
    )
}