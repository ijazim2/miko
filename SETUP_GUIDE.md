# kitty's premium — Android Setup Guide
## Step-by-Step: From Zero to Running App

---

## STEP 1 — Create the Project in Android Studio

1. Open **Android Studio Meerkat** (2024.3+)
2. **File → New → New Project**
3. Select **Empty Activity**
4. Fill in:
   - Name: `kitty's premium`
   - Package: `com.kittys.premium`
   - Save location: your choice
   - Language: **Kotlin**
   - Minimum SDK: **API 26 (Android 8.0)**
5. Click **Finish**

---

## STEP 2 — Replace gradle files

Copy these files from the code package into your project:

| File in code package       | Copy to                          |
|----------------------------|----------------------------------|
| `build.gradle.kts`         | `app/build.gradle.kts`           |
| `libs.versions.toml`       | `gradle/libs.versions.toml`      |

Then click **"Sync Now"** in the yellow banner.

---

## STEP 3 — Add your API keys (local.properties)

Open `local.properties` (in project root, never commit this file).
Add these lines:

```properties
# Supabase — get from supabase.com → project → Settings → API
SUPABASE_URL=https://YOUR_PROJECT_ID.supabase.co
SUPABASE_ANON_KEY=your_supabase_anon_key_here

# Gemini AI — get from aistudio.google.com → Get API Key (FREE)
GEMINI_API_KEY=your_gemini_api_key_here
```

---

## STEP 4 — Set up Supabase (FREE)

1. Go to **supabase.com** → Create account → New project
2. Go to **SQL Editor** → run this SQL:

```sql
-- Users table
CREATE TABLE users (
  id           UUID PRIMARY KEY REFERENCES auth.users(id),
  full_name    TEXT NOT NULL,
  email        TEXT NOT NULL,
  phone        TEXT DEFAULT '',
  avatar_url   TEXT,
  role         TEXT DEFAULT 'customer',
  member_since TEXT DEFAULT to_char(now(), 'Mon YYYY'),
  is_premium   BOOLEAN DEFAULT false,
  order_count  INT DEFAULT 0,
  wishlist_count INT DEFAULT 0,
  review_count INT DEFAULT 0,
  created_at   TIMESTAMPTZ DEFAULT now()
);

-- Products table
CREATE TABLE products (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name             TEXT NOT NULL,
  description      TEXT,
  brand            TEXT,
  vendor_id        UUID,
  vendor_name      TEXT,
  vendor_rating    FLOAT DEFAULT 0,
  price_lkr        INT NOT NULL,
  original_price   INT,
  discount_percent INT DEFAULT 0,
  images           TEXT[] DEFAULT '{}',
  sizes            TEXT[] DEFAULT '{}',
  colors           TEXT[] DEFAULT '{}',
  category         TEXT,
  tags             TEXT[] DEFAULT '{}',
  rating           FLOAT DEFAULT 0,
  review_count     INT DEFAULT 0,
  stock_qty        INT DEFAULT 0,
  created_at       TIMESTAMPTZ DEFAULT now()
);

-- Cart table
CREATE TABLE cart (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID REFERENCES users(id) ON DELETE CASCADE,
  product_id UUID,
  name       TEXT,
  image_url  TEXT,
  brand      TEXT,
  size       TEXT,
  color      TEXT,
  price_lkr  INT,
  quantity   INT DEFAULT 1,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Wishlist table
CREATE TABLE wishlist (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID REFERENCES users(id) ON DELETE CASCADE,
  product_id UUID,
  created_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(user_id, product_id)
);

-- Child profiles table
CREATE TABLE child_profiles (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID REFERENCES users(id) ON DELETE CASCADE,
  name          TEXT NOT NULL,
  gender        TEXT,
  date_of_birth DATE,
  height_cm     INT,
  weight_kg     FLOAT,
  current_size  TEXT,
  created_at    TIMESTAMPTZ DEFAULT now()
);

-- Auto-create user profile on signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.users (id, full_name, email)
  VALUES (
    new.id,
    COALESCE(new.raw_user_meta_data->>'full_name', 'User'),
    new.email
  );
  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();

-- Enable Row Level Security
ALTER TABLE users          ENABLE ROW LEVEL SECURITY;
ALTER TABLE cart           ENABLE ROW LEVEL SECURITY;
ALTER TABLE wishlist       ENABLE ROW LEVEL SECURITY;
ALTER TABLE child_profiles ENABLE ROW LEVEL SECURITY;

-- Policies
CREATE POLICY "Users can read own data" ON users
  FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update own data" ON users
  FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users manage own cart" ON cart
  FOR ALL USING (auth.uid() = user_id);

CREATE POLICY "Users manage own wishlist" ON wishlist
  FOR ALL USING (auth.uid() = user_id);

CREATE POLICY "Products are public" ON products
  FOR SELECT USING (true);
```

3. Go to **Authentication → Providers** → Enable **Email** and **Google**

---

## STEP 5 — Add fonts (FREE from Google Fonts)

1. Create folder: `app/src/main/res/font/`
2. Download from **fonts.google.com**:
   - [Outfit](https://fonts.google.com/specimen/Outfit) → save as:
     - `outfit_regular.ttf`
     - `outfit_semibold.ttf`
     - `outfit_bold.ttf`
   - [DM Sans](https://fonts.google.com/specimen/DM+Sans) → save as:
     - `dm_sans_regular.ttf`
     - `dm_sans_medium.ttf`

---

## STEP 6 — Add placeholder drawables

Create these vector files in `app/src/main/res/drawable/`:

**placeholder_product.xml** (grey rounded rectangle):
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#E8EDF5" />
    <corners android:radius="16dp" />
</shape>
```

For all icon files (`ic_cart.xml`, `ic_search.xml`, etc.):
- Go to Android Studio → **File → New → Vector Asset**
- Choose from built-in Material icons (they're free!)

| File needed             | Material Icon name    |
|-------------------------|-----------------------|
| `ic_home.xml`           | home                  |
| `ic_search.xml`         | search                |
| `ic_cart.xml`           | shopping_cart         |
| `ic_profile.xml`        | person                |
| `ic_ai_sparkle.xml`     | auto_awesome          |
| `ic_wishlist.xml`       | favorite_border       |
| `ic_wishlist_filled.xml`| favorite              |
| `ic_notification_bell.xml` | notifications      |
| `ic_arrow_right.xml`    | chevron_right         |
| `ic_arrow_back.xml`     | arrow_back            |
| `ic_star_filled.xml`    | star                  |
| `ic_star_outline.xml`   | star_border           |
| `ic_filter.xml`         | tune                  |
| `ic_email.xml`          | email                 |
| `ic_lock.xml`           | lock                  |
| `ic_eye.xml`            | visibility            |
| `ic_eye_off.xml`        | visibility_off        |
| `ic_person.xml`         | person_outline        |
| `ic_phone.xml`          | phone                 |
| `ic_tag.xml`            | local_offer           |
| `ic_location.xml`       | location_on           |
| `ic_payment.xml`        | credit_card           |
| `ic_language.xml`       | language              |
| `ic_help.xml`           | help_outline          |
| `ic_info.xml`           | info_outline          |

---

## STEP 7 — Copy all .kt files

Copy the Kotlin files into Android Studio matching this structure:

```
app/src/main/java/com/kittys/premium/
├── MainActivity.kt
├── KittysApp.kt                     ← in DependencyInjection.kt
│
├── core/
│   ├── common/Result.kt             ← at bottom of Models.kt
│   ├── di/DependencyInjection.kt
│   └── navigation/NavGraph.kt
│
├── domain/
│   └── model/Models.kt
│
├── data/
│   └── repository/Repositories.kt
│
├── features/
│   ├── splash/SplashScreen.kt
│   ├── auth/
│   │   ├── LoginScreen.kt
│   │   ├── SignUpScreen.kt
│   │   └── AuthViewModel.kt
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── product/ProductDetailScreen.kt
│   ├── cart/CartScreen.kt
│   ├── profile/ProfileScreen.kt
│   ├── ai/
│   │   ├── AIStylistScreen.kt
│   │   └── GeminiService.kt
│   └── ViewModels.kt               ← split into separate files later
│
└── ui/
    ├── theme/
    │   ├── Theme.kt
    │   └── Typography.kt
    └── components/NeuComponents.kt
```

**Note:** The `ViewModels.kt` file contains 4 ViewModels in one file for
convenience. In Android Studio, split each `package` block into its own file:
- `CartViewModel.kt` → `features/cart/`
- `ProfileViewModel.kt` → `features/profile/`
- `AIViewModel.kt` → `features/ai/`

---

## STEP 8 — Run the app!

1. Connect your Android phone (enable **Developer Options → USB Debugging**)
   OR use the built-in emulator (API 26+)
2. Click the green **▶ Run** button
3. The app will build and launch on your device

---

## WHAT TO REPLACE (marked in code)

Search for these comments in the code and replace with your own assets:

| Comment                          | What to replace with             |
|----------------------------------|----------------------------------|
| `// REPLACE with your logo`      | Your `ic_logo.xml` or PNG        |
| `placeholder_product`            | Your product placeholder image   |
| `// REPLACE: Image(...)`         | Real logo Image() composable     |
| Text("K") in circles             | Your actual logo drawable        |
| `Text("←")` back arrows         | Icon with `ic_arrow_back`        |
| `Text("→")` send button         | Icon with your send drawable     |

---

## FREE APIs USED

| Service      | Cost  | What for                        |
|--------------|-------|---------------------------------|
| Supabase     | FREE  | Database, Auth, Storage         |
| Gemini AI    | FREE* | AI Stylist chat, size prediction|
| Firebase     | FREE* | Push notifications              |
| Google Fonts | FREE  | Outfit + DM Sans fonts          |
| Google Auth  | FREE  | Google Sign-In                  |

*Free tier — generous limits for student project

---

## QUICK CHECKLIST BEFORE FIRST RUN

- [ ] `local.properties` has SUPABASE_URL and SUPABASE_ANON_KEY
- [ ] `local.properties` has GEMINI_API_KEY
- [ ] Supabase SQL tables created
- [ ] Font files added to `res/font/`
- [ ] `placeholder_product.xml` added to `res/drawable/`
- [ ] At least the basic icons added (home, search, cart, profile, ai)
- [ ] Gradle sync completed with no errors

---

*kitty's premium — Built with Kotlin + Jetpack Compose + Supabase + Gemini AI*
*Sri Lanka's Premium Kids Fashion Ecosystem*
