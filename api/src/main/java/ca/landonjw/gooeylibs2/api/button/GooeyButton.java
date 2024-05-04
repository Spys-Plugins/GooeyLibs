package ca.landonjw.gooeylibs2.api.button;

import com.google.common.collect.Lists;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GooeyButton extends ButtonBase {

    private final Consumer<ButtonAction> onClick;

    protected GooeyButton(@Nonnull ItemStack display, @Nullable Consumer<ButtonAction> onClick) {
        super(display);
        this.onClick = onClick;
    }

    @Override
    public void onClick(@Nonnull ButtonAction action) {
        if (onClick != null) onClick.accept(action);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static GooeyButton of(ItemStack stack) {
        return builder()
                .display(stack)
                .build();
    }

    public static class Builder {

        protected ItemStack display;
        protected Component title;
        protected Collection<Component> lore = Lists.newArrayList();
        protected Consumer<ButtonAction> onClick;
        protected Set<FlagType> hideFlags = new LinkedHashSet<>();

        public Builder display(@Nonnull ItemStack display) {
            this.display = display;
            return this;
        }

        public Builder title(@Nullable String title) {
            if(title == null) {
                return this;
            }

            return this.title(Component.literal(title));
        }

        public Builder title(@Nullable Component title) {
            this.title = title;
            return this;
        }

        public Builder lore(@Nullable Collection<String> lore) {
            if(lore == null) {
                return this;
            }

            this.lore = lore.stream().map(Component::literal).collect(Collectors.toList());
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> Builder lore(Class<T> type, @Nullable Collection<T> lore) {
            if(lore == null) {
                return this;
            }

            if(Component.class.isAssignableFrom(type)) {
                this.lore = (Collection<Component>) lore;
                return this;
            } else if(String.class.isAssignableFrom(type)) {
                return this.lore((Collection<String>) lore);
            }

            throw new UnsupportedOperationException("Invalid Type: " + type.getName());
        }

        public Builder hideFlags(FlagType... flags) {
            this.hideFlags.addAll(Arrays.asList(flags));
            return this;
        }

        public Builder onClick(@Nullable Consumer<ButtonAction> behaviour) {
            this.onClick = behaviour;
            return this;
        }

        public Builder onClick(@Nullable Runnable behaviour) {
            this.onClick = (behaviour != null) ? (action) -> behaviour.run() : null;
            return this;
        }

        public GooeyButton build() {
            validate();
            return new GooeyButton(buildDisplay(), onClick);
        }

        protected void validate() {
            if (display == null) throw new IllegalStateException("button display must be defined");
        }

        protected ItemStack buildDisplay() {
            if (title != null) {
                MutableComponent result = Component.empty()
                        .setStyle(Style.EMPTY.withItalic(false))
                        .append(this.title);
                display.setHoverName(result);
            }

            if (!lore.isEmpty()) {
                ListTag nbtLore = new ListTag();
                for (Component line : lore) {
                    MutableComponent result = Component.empty()
                            .setStyle(Style.EMPTY.withItalic(false))
                            .append(line);
                    nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(result)));
                }
                display.getOrCreateTagElement("display").put("Lore", nbtLore);
            }

            if (!this.hideFlags.isEmpty() && display.hasTag())
            {
                if (this.hideFlags.contains(FlagType.Reforged) || this.hideFlags.contains(FlagType.All))
                {
                    display.getOrCreateTag().putString("tooltip", "");
                }
                if (this.hideFlags.contains(FlagType.Generations) || this.hideFlags.contains(FlagType.All))
                {
                    display.getOrCreateTag().putBoolean("HideTooltip", true);
                }
                int value = 0;
                for (FlagType flag : this.hideFlags)
                {
                    value += flag.getValue();
                }
                display.getOrCreateTag().putInt("HideFlags", value);
            }
            return display;
        }

    }

}